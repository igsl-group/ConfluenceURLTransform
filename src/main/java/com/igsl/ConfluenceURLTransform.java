package com.igsl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.igsl.config.Config;
import com.igsl.export.cloud.BaseExport;
import com.igsl.export.cloud.CloudConfluencePages;
import com.igsl.export.cloud.CloudConfluenceSpaces;
import com.igsl.export.cloud.model.ConfluenceBody;
import com.igsl.export.cloud.model.ConfluenceBodyType;
import com.igsl.export.cloud.model.ConfluencePage;
import com.igsl.export.cloud.model.ConfluencePageTemplate;
import com.igsl.export.cloud.model.ConfluencePages;
import com.igsl.export.cloud.model.ConfluenceVersion;
import com.igsl.export.dc.ObjectExport;
import com.igsl.handler.Handler;
import com.igsl.handler.HandlerResult;
import com.igsl.rest.RESTUtil;
import com.igsl.rest.UpdatePage;

public class ConfluenceURLTransform {
	private static final Logger LOGGER = LogManager.getLogger(ConfluenceURLTransform.class);

	private static final String COMMAND_TRANSFORM_URL = "url";
	private static final String COMMAND_DC_EXPORT = "dcexport";
	private static final String COMMAND_CLOUD_EXPORT = "cloudexport";
	private static final String COMMAND_POST_MIGRATE = "postmigrate";
	private static final String COMMAND_MIGRATE_PAGE_TEMPLATE = "pagetemplate";
	private static final String COMMAND_TEST = "test";

	private static final String QUERY = "SELECT bc.BODYCONTENTID, bc.BODY, s.SPACEKEY, c.TITLE "
			+ "FROM BODYCONTENT bc " + "JOIN CONTENT c ON c.CONTENTID = bc.CONTENTID AND c.PREVVER IS NULL "
			+ "JOIN SPACES s ON s.SPACEID = c.SPACEID " + "WHERE bc.BODY LIKE '%href=\"%'";
	private static final String UPDATE = "UPDATE BODYCONTENT SET BODY = ? WHERE BODYCONTENTID = ?";
	private static final ObjectMapper OM_CONFIG = JsonMapper.builder().enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
			.build();
	private static final Pattern URL_PATTERN = Pattern
			.compile("(<a\\s+(?:[^>]*?)?href\\s*=\\s*([\"']))(.*?)(\\2(?:[^>]*?)>(.*?)<\\/a>)");
	// Capturing groups 1 + 3 + 4 forms the whole pattern
	private static final int GROUP_BEFORE_HREF = 1;
	private static final int GROUP_HREF = 3;
	private static final int GROUP_AFTER_HREF = 4;
	private static final int GROUP_TEXT = 5;
	private static final String OUTPUT_URL_ERRORS = "URL Errors.csv";
	private static final String OUTPUT_URL_UPDATED = "URL Updated.csv";
	private static final String OUTPUT_URL_IGNORED = "URL Ignored.csv";
	private static final String OUTPUT_PAGE_POST_MIGRATE = "Post Migrate.csv";

	private static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception ex) {
				Log.error(LOGGER, "Failed to close connection", ex);
			}
		}
	}

	private static void urlTransform(Config config) {
		int pageCount = 0; // No. of pages found with URLs
		int pageUpdatedCount = 0; // No. of pages updated
		int pagePostMigrateCount = 0; // No. of pages requiring post migration
		int urlCount = 0; // No. of URLs found
		int urlError = 0; // No. of invalid URLs
		int urlIgnoredCount = 0; // No. of URLs ignored
		int urlUpdatedCount = 0; // No. of URLs updated
		int urlPostMigrateCount = 0; // No. of URLs requiring post migration
		try {
			long startTime = System.currentTimeMillis();
			if (config.getUrlTransform().isPerformUpdate()) {
				Log.info(LOGGER, "performUpdate is true, database will be updated");
			} else {
				Log.info(LOGGER, "performUpdate is false, database will not be updated");
			}
			String urlList = config.getOutputDirectory().resolve(OUTPUT_URL_UPDATED).toFile().getAbsolutePath();
			String ignoreList = config.getOutputDirectory().resolve(OUTPUT_URL_IGNORED).toFile().getAbsolutePath();
			String errorList = config.getOutputDirectory().resolve(OUTPUT_URL_ERRORS).toFile().getAbsolutePath();
			String pageList = config.getOutputDirectory().resolve(OUTPUT_PAGE_POST_MIGRATE).toFile().getAbsolutePath();
			try (CSVPrinter urlPrinter = new CSVPrinter(new FileWriter(urlList),
					CSV.getCSVWriteFormat(Arrays.asList("POSTMIGRATE", "SPACEKEY", "TITLE", "BODYCONTENTID", "HANDLER",
							"FROM", "TO")));
					CSVPrinter ignorePrinter = new CSVPrinter(new FileWriter(ignoreList),
							CSV.getCSVWriteFormat(Arrays.asList("SPACEKEY", "TITLE", "BODYCONTENTID", "URL")));
					CSVPrinter errorPrinter = new CSVPrinter(new FileWriter(errorList),
							CSV.getCSVWriteFormat(Arrays.asList("SPACEKEY", "TITLE", "BODYCONTENTID", "URL",
									"ERRORMESSAGE", "HANDLER")));
					CSVPrinter pagePrinter = new CSVPrinter(new FileWriter(pageList),
							CSV.getCSVWriteFormat(Arrays.asList("SPACEKEY", "TITLE")));) {
				Log.info(LOGGER, "URLs updated will be written to: " + urlList);
				Log.info(LOGGER, "URLs ignored will be written to: " + ignoreList);
				Log.info(LOGGER, "Ignored URL list will be written to: " + errorList);
				Log.info(LOGGER, "Page requiring post migrate will be written to: " + pageList);
				// Create handlers
				List<Handler> handlers = new ArrayList<>();
				for (String handlerName : config.getHandler().getUrlTransform()) {
					try {
						Handler h = (Handler) Class.forName(handlerName).getDeclaredConstructor(Config.class)
								.newInstance(config);
						handlers.add(h);
					} catch (Exception ex) {
						Log.error(LOGGER, "Unable to create handler " + handlerName, ex);
					}
				}
				Connection confluenceConn = config.getConnections().getConfluenceConnection();
				try (PreparedStatement query = confluenceConn.prepareStatement(QUERY)) {
					try (ResultSet rs = query.executeQuery()) {
						while (rs.next()) {
							pageCount++;
							String id = rs.getString(1);
							Log.debug(LOGGER, "Processing body content ID: " + id);
							String body = rs.getString(2);
							String spaceKey = rs.getString(3);
							String title = rs.getString(4);
							Matcher matcher = URL_PATTERN.matcher(body);
							StringBuilder sb = new StringBuilder();
							boolean changed = false;
							boolean postMigrate = false;
							while (matcher.find()) {
								urlCount++;
								String tag = matcher.group(0);
								String urlString = matcher.group(GROUP_HREF);
								String urlText = matcher.group(GROUP_TEXT);
								String handlerName = "N/A";
								try {
									String urlDecoded = StringEscapeUtils.unescapeHtml4(urlString);
									URI uri = new URI(urlDecoded);
									boolean accepted = false;
									for (Handler handler : handlers) {
										if (handler.accept(uri)) {
											handlerName = handler.getClass().getCanonicalName();
											HandlerResult hr = handler.handle(uri, urlText);
											switch (hr.getResultType()) {
											case ERROR:
												urlError++;
												CSV.printRecord(errorPrinter, spaceKey, title, id, urlString,
														hr.getErrorMessage(), handlerName);
												break;
											case TAG:
												accepted = true;
												changed = true;
												urlUpdatedCount++;
												Log.debug(LOGGER, handler.getClass() + ": " + "ID: [" + id + "] "
														+ "From: [" + tag + "] " + "To: [" + hr.getTag() + "]");
												// Replace the whole match
												matcher.appendReplacement(sb, Matcher.quoteReplacement(hr.getTag()));
												CSV.printRecord(urlPrinter, handler.needPostMigrate(), spaceKey, title,
														id, handler.getClass(), tag, hr.getTag());
												break;
											case URI:
												accepted = true;
												changed = true;
												urlUpdatedCount++;
												String resultUrl = StringEscapeUtils
														.escapeHtml4(hr.getUri().toString());
												Log.debug(LOGGER,
														handler.getClass() + ": " + "ID: [" + id + "] " + "From URL: ["
																+ urlString + "] " + "Decoded URL: [" + urlDecoded
																+ "] " + "Path: [" + uri.getPath() + "] " + "Query: ["
																+ uri.getQuery() + "] " + "To URL: ["
																+ hr.getUri().toString() + "] " + "To Escaped URL: ["
																+ resultUrl + "]");
												// Replace URL only
												matcher.appendReplacement(sb, "$" + GROUP_BEFORE_HREF
														+ Matcher.quoteReplacement(resultUrl) + "$" + GROUP_AFTER_HREF);
												CSV.printRecord(urlPrinter, handler.needPostMigrate(), spaceKey, title,
														id, handlerName, urlString, resultUrl);
												break;
											}
											if (accepted && handler.needPostMigrate()) {
												postMigrate = true;
												urlPostMigrateCount++;
											}
											break; // Stop after a handler accepts the URL
										}
									}
									if (!accepted) {
										urlIgnoredCount++;
										CSV.printRecord(ignorePrinter, spaceKey, title, id, urlString);
										Log.debug(LOGGER, "Unaccepted URL: " + urlString);
									}
								} catch (Exception ex) {
									Log.debug(LOGGER, "Ignoring invalid URI: " + urlString);
									urlError++;
									CSV.printRecord(errorPrinter, spaceKey, title, id, urlString, ex.getMessage(),
											handlerName);
								}
							} // While matcher.find
							matcher.appendTail(sb);
							if (postMigrate) {
								pagePostMigrateCount++;
								CSV.printRecord(pagePrinter, spaceKey, title);
							}
							if (changed) {
								pageUpdatedCount++;
								Log.debug(LOGGER, "ID: " + id + " " + "Source: [" + body + "] " + "Result: ["
										+ sb.toString() + "]");
								if (config.getUrlTransform().isPerformUpdate()) {
									try (PreparedStatement update = confluenceConn.prepareStatement(UPDATE)) {
										update.setString(1, sb.toString());
										update.setString(2, id);
										int count = update.executeUpdate();
										if (count == 1) {
											Log.debug(LOGGER, "ID: " + id + " Updated successfully");
											confluenceConn.commit();
										} else {
											Log.debug(LOGGER, "ID: " + id
													+ " Update failed, more than 1 row got updated, rolling back");
											confluenceConn.rollback();
										}
									} catch (Exception ex) {
										ex.printStackTrace();
										Log.error(LOGGER, "ID: " + id + " Update failed, rolling back");
										confluenceConn.rollback();
									}
								}
								/*
								 * NOTE: Need to flush cache on content objects or restart server to show new
								 * values.
								 * 
								 * Drafts are also stored in BODYCONTENT and need to be updated. Otherwise you
								 * will open the editor and see old link values. But since drafts are not
								 * migrated... leave this for later.
								 * 
								 * Reference:
								 * https://confluence.atlassian.com/confkb/how-to-bulk-update-confluence-content
								 * -through-the-database-to-replace-old-macros-with-new-ones-1018780615.html
								 * https://confluence.atlassian.com/confkb/how-do-drafts-work-on-confluence-
								 * 938043306.html
								 * 
								 * To find the drafts:
								 * 
								 * SELECT cp.CONTENTID, bc.body, c.* FROM CONTENTPROPERTIES cp JOIN CONTENT c ON
								 * c.CONTENTID = cp.CONTENTID and c.CONTENT_STATUS = 'draft' JOIN BODYCONTENT bc
								 * ON bc.CONTENTID = cp.CONTENTID JOIN CONTENTPROPERTIES cpMain ON cp.STRINGVAL
								 * = cpMain.STRINGVAL AND cpMain.PROPERTYNAME = 'share-id' WHERE cp.PROPERTYNAME
								 * = 'share-id' AND cpMain.CONTENTID = <ID updated> AND cp.CONTENTID !=
								 * cpMain.CONTENTID;
								 * 
								 * The draft record remains after publishing.
								 */
							} // If changed
						} // ResultSet loop
					}
				} // Try PreparedStatement
			} catch (Exception ex) {
				Log.error(LOGGER, "Error", ex);
			}
			long stopTime = System.currentTimeMillis();
			Log.info(LOGGER,
					"Execution time: From " + startTime + " to " + stopTime + ", elapsed: " + (stopTime - startTime));
			Log.info(LOGGER, "Page count: " + pageCount);
			Log.info(LOGGER, "Page updated: " + pageUpdatedCount);
			Log.info(LOGGER, "URL count: " + urlCount);
			Log.info(LOGGER, "Invalid URL: " + urlError);
			Log.info(LOGGER, "URL updated: " + urlUpdatedCount);
			Log.info(LOGGER, "URL ignored: " + urlIgnoredCount);
			Log.info(LOGGER, "Page requiring post migration (among page updated): " + pagePostMigrateCount);
			Log.info(LOGGER, "URL requiring post migration (among url updated): " + urlPostMigrateCount);
		} catch (Exception ex) {
			Log.error(LOGGER, "Error", ex);
		}
	}

	private static void exportCloudObjects(Config config) throws Exception {
		String dcDir = Console.readLine("DC Export Directory: ");
		Path dcPath = Paths.get(dcDir);
		if (!Files.exists(dcPath) || !Files.isDirectory(dcPath)) {
			throw new Exception("\"" + dcDir + "\" is not a valid directory");
		}
		config.setDcExportDirectory(dcPath);
		List<BaseExport<?>> exporters = new ArrayList<>();
		for (String exporterName : config.getHandler().getCloud()) {
			try {
				BaseExport<?> exporter = (BaseExport<?>) Class.forName(exporterName).getDeclaredConstructor()
						.newInstance();
				exporters.add(exporter);
			} catch (Exception ex) {
				Log.error(LOGGER, "Unable to create Cloud exporter " + exporterName, ex);
			}
		}
		for (BaseExport<?> exporter : exporters) {
			try {
				Path[] p = exporter.exportObjects(config);
				Log.info(LOGGER, exporter.getClass().getSimpleName() + ": objects written to "
						+ p[0].toFile().getAbsolutePath());
				Log.info(LOGGER, exporter.getClass().getSimpleName() + ": mappings written to "
						+ p[1].toFile().getAbsolutePath());
			} catch (Exception ex) {
				Log.error(LOGGER, "Error", ex);
			}
		}
	}

	private static void exportDCObjects(Config config) throws Exception {
		List<ObjectExport> exporters = new ArrayList<>();
		for (String exporterName : config.getHandler().getDc()) {
			try {
				ObjectExport exporter = (ObjectExport) Class.forName(exporterName).getDeclaredConstructor()
						.newInstance();
				exporters.add(exporter);
			} catch (Exception ex) {
				Log.error(LOGGER, "Unable to create DC exporter " + exporterName, ex);
			}
		}
		for (ObjectExport exporter : exporters) {
			try {
				exporter.setConfig(config);
				Path p = exporter.exportObjects();
				Log.info(LOGGER,
						exporter.getClass().getSimpleName() + ": objects written to " + p.toFile().getAbsolutePath());
			} catch (Exception ex) {
				Log.error(LOGGER, "Error", ex);
			}
		}
	}

	private static void postMigrate(Config config) throws Exception {
		String dcDir = Console.readLine("DC Export Directory: ");
		Path dcPath = Paths.get(dcDir);
		if (!Files.exists(dcPath) || !Files.isDirectory(dcPath)) {
			throw new Exception("\"" + dcDir + "\" is not a valid directory");
		}
		config.setDcExportDirectory(dcPath);
		String cloudDir = Console.readLine("Cloud Export Directory: ");
		Path cloudPath = Paths.get(cloudDir);
		if (!Files.exists(cloudPath) || !Files.isDirectory(cloudPath)) {
			throw new Exception("\"" + cloudDir + "\" is not a valid directory");
		}
		config.setCloudExportDirectory(cloudPath);
		String urlDir = Console.readLine("URL Export Directory: ");
		Path urlPath = Paths.get(urlDir);
		if (!Files.exists(urlPath) || !Files.isDirectory(urlPath)) {
			throw new Exception("\"" + urlDir + "\" is not a valid directory");
		}
		config.setUrlExportDirectory(urlPath);
		// Read spaces
		CloudConfluenceSpaces spaces = new CloudConfluenceSpaces();
		Map<String, String> spaceMapping = spaces.readCloudUniqueKeyToIdMapping(cloudPath);
		// Read URL post migrate CSV
		Path postMigrateCSV = Paths.get(config.getUrlExportDirectory().toFile().getAbsolutePath(),
				OUTPUT_PAGE_POST_MIGRATE);
		// Initialize handlers
		List<Handler> handlers = new ArrayList<>();
		for (String handlerName : config.getHandler().getPostMigrate()) {
			try {
				Handler h = (Handler) Class.forName(handlerName).getDeclaredConstructor(Config.class)
						.newInstance(config);
				handlers.add(h);
			} catch (Exception ex) {
				Log.error(LOGGER, "Unable to create handler " + handlerName, ex);
			}
		}
		// Output file
		Path outputPath = Paths.get(config.getOutputDirectory().toFile().getAbsolutePath(), "Cloud URL Updated.csv");
		// Process post migrate pages
		try (FileReader fr = new FileReader(postMigrateCSV.toFile());
				FileWriter output = new FileWriter(outputPath.toFile());
				CSVParser postMigrateList = new CSVParser(fr, CSV.getCSVReadFormat());
				CSVPrinter outputPrinter = new CSVPrinter(output,
						CSV.getCSVWriteFormat(Arrays.asList("SPACEKEY", "TITLE", "ERROR", "HANDLER", "FROM", "TO")))) {
			CloudConfluencePages pages = new CloudConfluencePages();
			// Read page content
			for (CSVRecord r : postMigrateList.getRecords()) {
				String spaceKey = r.get(0);
				String spaceId = spaceMapping.get(spaceKey);
				String title = r.get(1);
				// Read page content
				pages.setSpaceId(spaceId);
				pages.setTitle(title);
				pages.setGetVersions(false);
				List<ConfluencePages> pagesList = pages.getObjects(config);
				if (pagesList.size() == 1 && pagesList.get(0).getResults().size() == 1) {
					// Patch URLs
					ConfluencePage page = pagesList.get(0).getResults().get(0);
					String content = page.getBody().getStorage().getValue();
					Log.debug(LOGGER, "Post migrate page in space " + spaceKey + "(" + spaceId + ") title " + title);
					Log.debug(LOGGER, "Content: " + content);
					// Parse content, find URL patterns
					Matcher matcher = URL_PATTERN.matcher(content);
					StringBuilder sb = new StringBuilder();
					boolean changed = false;
					while (matcher.find()) {
						String tag = matcher.group(0);
						String urlString = matcher.group(GROUP_HREF);
						String urlText = matcher.group(GROUP_TEXT);
						String handlerName = "N/A";
						try {
							String urlDecoded = StringEscapeUtils.unescapeHtml4(urlString);
							URI uri = new URI(urlDecoded);
							boolean accepted = false;
							for (Handler handler : handlers) {
								if (handler.accept(uri)) {
									handlerName = handler.getClass().getCanonicalName();
									Log.debug(LOGGER, "Handler " + handlerName + " accepts " + urlString);
									HandlerResult hr = handler.handle(uri, urlText);
									switch (hr.getResultType()) {
									case ERROR:
										CSV.printRecord(outputPrinter, spaceKey, title, hr.getErrorMessage(),
												handlerName, urlString, "");
										break;
									case TAG:
										accepted = true;
										changed = true;
										Log.debug(LOGGER, handler.getClass() + ": " + "From: [" + tag + "] " + "To: ["
												+ hr.getTag() + "]");
										// Replace the whole match
										matcher.appendReplacement(sb, Matcher.quoteReplacement(hr.getTag()));
										CSV.printRecord(outputPrinter, spaceKey, title, "", handlerName, tag,
												hr.getTag());
										break;
									case URI:
										accepted = true;
										changed = true;
										String resultUrl = StringEscapeUtils.escapeHtml4(hr.getUri().toString());
										Log.debug(LOGGER,
												handler.getClass() + ": " + "From URL: [" + urlString + "] "
														+ "Decoded URL: [" + urlDecoded + "] " + "Path: ["
														+ uri.getPath() + "] " + "Query: [" + uri.getQuery() + "] "
														+ "To URL: [" + hr.getUri().toString() + "] "
														+ "To Escaped URL: [" + resultUrl + "]");
										// Replace URL only
										matcher.appendReplacement(sb, "$" + GROUP_BEFORE_HREF
												+ Matcher.quoteReplacement(resultUrl) + "$" + GROUP_AFTER_HREF);
										CSV.printRecord(outputPrinter, spaceKey, title, "", handlerName, urlString,
												resultUrl);
										break;
									}
									break; // Stop after a handler accepts the URL
								}
							}
							if (!accepted) {
								Log.debug(LOGGER, "No handlers accept " + urlString);
								CSV.printRecord(outputPrinter, spaceKey, title, "Ignored", "", urlString, urlString);
							}
						} catch (Exception ex) {
							Log.debug(LOGGER, "Ignoring invalid URI: " + urlString);
							CSV.printRecord(outputPrinter, spaceKey, title, "Invalid URI: " + ex.getMessage(),
									handlerName, urlString, "");
						}
					} // While matcher.find
					matcher.appendTail(sb);
					if (changed) {
						// Perform update
						Log.debug(LOGGER, spaceKey + " " + title + " from: " + content);
						Log.debug(LOGGER, spaceKey + " " + title + " to: " + sb.toString());
						if (config.getPostMigrate().isPerformUpdate()) {
							MultivaluedMap<String, Object> authHeader = RESTUtil.getCloudAuthenticationHeader(config);
							Map<String, Object> query = new HashMap<>();
							query.put("id", page.getId());
							query.put("status", "current");
							query.put("title", page.getTitle());
							ConfluenceBodyType bodyType = new ConfluenceBodyType();
							bodyType.setRepresentation("storage");
							bodyType.setValue(sb.toString());
							ConfluenceBody body = new ConfluenceBody();
							body.setStorage(bodyType);
							query.put("body", body);
							ConfluenceVersion version = page.getVersion();
							version.setNumber(version.getNumber() + 1);
							query.put("version", version);
							RESTUtil.invokeCloudRest(UpdatePage.class, config, "/pages/" + page.getId(), HttpMethod.PUT,
									authHeader, query, null, "startAt", HttpStatus.SC_OK);
							Log.info(LOGGER, "Page updated: " + page.getId());
							CSV.printRecord(outputPrinter, spaceKey, title, "Page Updated", "", "", "");
						}
					}
				} else {
					Log.error(LOGGER,
							"Unable to locate page in space " + spaceKey + "(" + spaceId + ") title " + title);
					CSV.printRecord(outputPrinter, spaceKey, title, "Unable to locate page in space", "", "", "");
				}
			}
		}
	}

	private static void migrateTemplate(Config config) throws Exception {
		// Input file
		String dcDir = Console.readLine("DC Export Directory: ");
		Path dcPath = Paths.get(dcDir);
		if (!Files.exists(dcPath) || !Files.isDirectory(dcPath)) {
			throw new Exception("\"" + dcDir + "\" is not a valid directory");
		}
		// Output file
		Path outputPath = Paths.get(config.getOutputDirectory().toFile().getAbsolutePath(), "Page Templates Migrated.csv");
		// Load DC blue prints
		com.igsl.export.dc.ConfluencePageTemplate pt = new com.igsl.export.dc.ConfluencePageTemplate();
		List<ObjectData> pageTemplates = pt.readObjects(dcPath);
		MultivaluedMap<String, Object> authHeader = RESTUtil.getCloudAuthenticationHeader(config);
		try (	FileWriter fw = new FileWriter(outputPath.toFile());
				CSVPrinter printer = new CSVPrinter(fw,
				CSV.getCSVWriteFormat(Arrays.asList("ID", "TEMPLATENAME", "RESULT")));) {
			for (ObjectData pageTemplate : pageTemplates) {
				String id = pageTemplate.getCsvRecord().get(com.igsl.export.dc.ConfluencePageTemplate.COL_ID);
				String name = pageTemplate.getCsvRecord().get(com.igsl.export.dc.ConfluencePageTemplate.COL_NAME);
				String description = pageTemplate.getCsvRecord().get(com.igsl.export.dc.ConfluencePageTemplate.COL_DESCRIPTION);
				String content = pageTemplate.getCsvRecord().get(com.igsl.export.dc.ConfluencePageTemplate.COL_CONTENT);
				boolean blueprint = 
					"1".equals(pageTemplate.getCsvRecord().get(com.igsl.export.dc.ConfluencePageTemplate.COL_BLUEPRINT));
				if (config.getPageTemplate().isPerformUpdate()) {
					// Create in Cloud
					Map<String, Object> query = new HashMap<>();
					ConfluencePageTemplate data = new ConfluencePageTemplate();
					if (blueprint) {
						// Blueprints gets overrode by system blueprints if they share the same name
						data.setName(name + " (Migrated)");
					} else {
						data.setName(name);
					}
					data.setDescription(description);
					data.setTemplateType("page");
					ConfluenceBody body = new ConfluenceBody();
					ConfluenceBodyType storage = new ConfluenceBodyType();
					storage.setRepresentation("storage");
					storage.setValue(content);
					body.setStorage(storage);
					data.setBody(body);
					try {
						RESTUtil.invokeCloudRest(ConfluencePageTemplate.class, config, "/wiki/rest/api/template",
								HttpMethod.POST, authHeader, query, data, "startAt", HttpStatus.SC_OK);
						CSV.printRecord(printer, Arrays.asList(id, name, "Migrated"));
					} catch (Exception ex) {
						CSV.printRecord(printer, Arrays.asList(id, name, ex.getMessage()));
					}
				} else {
					CSV.printRecord(printer, Arrays.asList(name, "Skipped"));
				}
			}
		}
	}
	
	private static void test(Config config) throws Exception {
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			Log.info(LOGGER, "Available Commands: " + 
					COMMAND_DC_EXPORT + " | " + 
					COMMAND_TRANSFORM_URL + " | " + 
					COMMAND_MIGRATE_PAGE_TEMPLATE + " | " + 
					COMMAND_CLOUD_EXPORT + " | " + 
					COMMAND_POST_MIGRATE + " | " + 
					COMMAND_TEST);
			try {
				String line = Console.readLine("Space-delimited Command(s): ");
				if (line != null && !line.isBlank()) {
					args = line.split("\\s");
				}
			} catch (IOException ioex) {
				Log.error(LOGGER, "Error", ioex);
				return;
			}
		}
		if (args.length == 0) {
			Log.error(LOGGER, "No command provided");
			return;
		}
		boolean needDCAccount = false;
		boolean needApiToken = false;
		boolean needConfluenceDB = false;
		boolean needJiraDB = false;
		for (String arg : args) {
			switch (arg.toLowerCase().trim()) {
			case COMMAND_TEST:
				break;
			case COMMAND_MIGRATE_PAGE_TEMPLATE:
				needApiToken = true;
				break;
			case COMMAND_POST_MIGRATE:
				needApiToken = true;
				break;
			case COMMAND_CLOUD_EXPORT:
				needApiToken = true;
				break;
			case COMMAND_DC_EXPORT:
				needJiraDB = true;
				needConfluenceDB = true;
				needDCAccount = true;
				break;
			case COMMAND_TRANSFORM_URL:
				needConfluenceDB = true;
				break;
			default:
				Log.warn(LOGGER, "Unrecognized command \"" + arg + "\"");
				break;
			}
		}
		Connection confluenceConn = null;
		Connection jiraConn = null;
		try {
			// Read config
			ObjectReader or = OM_CONFIG.readerFor(Config.class);
			Config config = new Config();
			try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Config.json")) {
				config = or.readValue(in);
			}
			config.validate();
			// Output directory
			Path outputDirectory = Paths.get(System.getProperty("user.dir"),
					new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
			if (!Files.exists(outputDirectory)) {
				outputDirectory = Files.createDirectories(outputDirectory);
			}
			if (!Files.isDirectory(outputDirectory)) {
				throw new Exception("Output directory cannot be created");
			}
			config.setOutputDirectory(outputDirectory);
			// Get password
			if (needConfluenceDB && config.getConnections().getConfluencePassword() == null) {
				Console.println("For Confluence database at: %1s\n",
						config.getConnections().getConfluenceConnectionString());
				if (config.getConnections().getConfluenceUser() == null) {
					config.getConnections().setConfluenceUser(Console.readLine("Confluence User: "));
				} else {
					Console.println("Confluence User: %1s", config.getConnections().getConfluenceUser());
				}
				config.getConnections()
						.setConfluencePassword(new String(Console.readPassword("Confluence Password: ")));
			}
			if (needJiraDB && config.getConnections().getJiraPassword() == null) {
				Console.println("For Jira database at: %1s\n", config.getConnections().getJiraConnectionString());
				if (config.getConnections().getJiraUser() == null) {
					config.getConnections().setJiraUser(Console.readLine("Jira User: "));
				} else {
					Console.println("Jira User: %1s", config.getConnections().getJiraUser());
				}
				config.getConnections().setJiraPassword(new String(Console.readPassword("Jira Password: ")));
			}
			if (needDCAccount && config.getDcExport().getJiraPassword() == null) {
				Console.println("For Jira server at: %1s\n", config.getDcExport().getJiraHost());
				if (config.getDcExport().getJiraUser() == null) {
					config.getDcExport().setJiraUser(Console.readLine("Jira User: "));
				} else {
					Console.println("Jira User: %1s", config.getConnections().getJiraUser());
				}
				config.getDcExport().setJiraPassword(new String(Console.readPassword("Jira Password: ")));
			}
			if (needDCAccount && config.getDcExport().getConfluencePassword() == null) {
				Console.println("For Confluence server at: %1s\n", config.getDcExport().getConfluenceHost());
				if (config.getDcExport().getConfluenceUser() == null) {
					config.getDcExport().setConfluenceUser(Console.readLine("Confluence User: "));
				} else {
					Console.println("Confluence User: %1s", config.getConnections().getConfluenceUser());
				}
				config.getDcExport().setConfluencePassword(new String(Console.readPassword("Confluence Password: ")));
			}
			if (needApiToken && config.getCloud().getApiToken() == null) {
				Console.println("For Cloud site: %1s", config.getCloud().getDomain());
				if (config.getCloud().getUserName() == null) {
					config.getCloud().setUserName(Console.readLine("Cloud User: "));
				} else {
					Console.println("Cloud User: %1s", config.getCloud().getUserName());
				}
				config.getCloud().setApiToken(Console.readLine("API Token: "));
			}
			// Create DB connections
			if (needConfluenceDB) {
				confluenceConn = DriverManager.getConnection(config.getConnections().getConfluenceConnectionString(),
						config.getConnections().getConfluenceUser(), config.getConnections().getConfluencePassword());
				confluenceConn.setAutoCommit(false);
				config.getConnections().setConfluenceConnection(confluenceConn);
			}
			if (needJiraDB) {
				jiraConn = DriverManager.getConnection(config.getConnections().getJiraConnectionString(),
						config.getConnections().getJiraUser(), config.getConnections().getJiraPassword());
				jiraConn.setAutoCommit(false);
				config.getConnections().setJiraConnection(jiraConn);
			}
			// Execute
			for (String arg : args) {
				switch (arg.toLowerCase().trim()) {
				case COMMAND_TEST:
					test(config);
					break;
				case COMMAND_MIGRATE_PAGE_TEMPLATE:
					migrateTemplate(config);
					break;
				case COMMAND_POST_MIGRATE:
					postMigrate(config);
					break;
				case COMMAND_CLOUD_EXPORT:
					exportCloudObjects(config);
					break;
				case COMMAND_DC_EXPORT:
					exportDCObjects(config);
					break;
				case COMMAND_TRANSFORM_URL:
					urlTransform(config);
					break;
				}
			}
		} catch (Exception ex) {
			Log.error(LOGGER, "Error", ex);
		} finally {
			closeConnection(jiraConn);
			closeConnection(confluenceConn);
		}
	}
}
