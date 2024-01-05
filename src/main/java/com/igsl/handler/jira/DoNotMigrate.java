package com.igsl.handler.jira;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.handler.HandlerResult;
import com.igsl.handler.URLPattern;

/**
 * To filter out unmigrated projects so their URLs won't change
 */
public class DoNotMigrate extends Jira {

	private static final Logger LOGGER = LogManager.getLogger();
	private static Map<String, String> ignoreProjects;	// Map of Project ID to Project Key
	
	private static void close(PreparedStatement ps) throws SQLException {
		if (ps != null) {
			ps.close();
		}
	}
	private static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}
	
	public DoNotMigrate(Config config) throws SQLException {
		super(config);
		if (ignoreProjects == null) {
			// Get list of projects not migrated
			ignoreProjects = new HashMap<>();
			String baseSql = "SELECT id, pkey FROM project WHERE pkey IN (%s)";
			String sql = String.format(baseSql,
	                config.getIgnoreProjectKeys()
	                	.stream()
	                	.map(v -> "?")
	                	.collect(Collectors.joining(",")));		
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = config.getConnections().getJiraConnection().prepareStatement(sql);
				int index = 1;
				for (String key : config.getIgnoreProjectKeys()) {
					ps.setString(index, key);
					index++;
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					ignoreProjects.put(rs.getString(1), rs.getString(2));
				}
			} finally {
				close(rs);
				close(ps);
			}
		}
	}
	
	private static abstract class PatternCheck {
		protected static final Logger LOGGER = LogManager.getLogger();
		protected URLPattern pattern;
		protected Connection connection = null;
		protected PreparedStatement ps = null;
		protected ResultSet rs = null;
		public PatternCheck(URLPattern pattern) {
			this.pattern = pattern;
		}
		public final URLPattern getURLPattern() {
			return pattern;
		}
		protected final boolean checkProjectId(String projectId) {
			return ignoreProjects.containsKey(projectId);
		}
		protected final boolean checkProjectKey(
				Config config, String sql, int projectKeyColumn, Object... param) 
				throws SQLException {
			boolean result = false;
			try {
				connection = config.getConnections().getJiraConnection();
				ps = connection.prepareStatement(sql);
				int index = 1;
				for (Object o : param) {
					ps.setObject(index, o);
					index++;
				}
				rs = ps.executeQuery();
				if (rs.next()) {
					String projectKey = rs.getString(projectKeyColumn);
					result = config.getIgnoreProjectKeys().contains(projectKey);
				}
			} finally {
				close(rs);
				close(ps);
			}
			return result;
		}
		protected final String getPathItem(URI uri, int group) {
			if (pattern.match(uri)) {
				Matcher m = pattern.getPathMatcher(uri);
				if (m.matches()) {
					return m.group(group);
				}
			}
			return null;
		}
		protected final String getQueryItem(URI uri, String name) {
			if (pattern.match(uri)) {
				Matcher m = pattern.getQueryMatchers(uri).get(name);
				if (m.find()) {
					return m.group(1);
				}
			}
			return null;
		}
		public abstract boolean accept(Config config, URI uri) 
				throws SQLException;
	}

	/*
	 * https://jira.pccwglobal.com/secure/RapidBoard.jspa?rapidView=464
	 * https://jira.pccwglobal.com/secure/RapidBoard.jspa?projectKey=DUX&amp;rapidView=513&amp;view=planning
	 * http://jira.pccwglobal.com/browse/INP-225?filter=xxx
	 * https://jira.pccwglobal.com/issues/?jql=labels+%3D+company-verification
	 * https://jira.pccwglobal.com/projects/CCTI/issues
	 * https://jira.pccwglobal.com/secure/CreateIssue.jspa?pid=13754&amp;issuetype=11300
	 * https://jira.pccwglobal.com/secure/Dashboard.jspa
	 * https://jira.pccwglobal.com/secure/Dashboard.jspa?pageId=15207
	 * https://jira.pccwglobal.com/secure/Dashboard.jspa?selectPageId=11100
	 * https://jira.pccwglobal.com/secure/DataplaneReport!default.jspa?report=2197480d-3e31-40f1-b231-04338e0b07a2&amp;action=run
	 * https://jira.pccwglobal.com/secure/DeleteLink.jspa?id=57561&amp;sourceId=55244&amp;linkType=10401&amp;atl_token=BET4-VRG0-B6WC-EH30_4bb8d9baed3f12ac342755ac699f493fafaafb10_lin
	 * https://jira.pccwglobal.com/secure/EditFilter!default.jspa?filterId=23005
	 * https://jira.pccwglobal.com/secure/IssueNavigator.jspa?reset=true&amp;jqlQuery=Filter+%3D+%22MS+ART+PI%236+PI+Objective+-+Mercury%22+&amp;src=confmacro
	 * https://jira.pccwglobal.com/secure/PortfolioPlanView.jspa?id=20
	 * https://jira.pccwglobal.com/secure/ReleaseNote.jspa?projectId=10902&amp;version=13235
	 * https://jira.pccwglobal.com/secure/attachment/112618/112618_IPCE-console+SR.png
	 * https://jira.pccwglobal.com/servicedesk/customer/portal/52
	 * https://jira.pccwglobal.com/secure/project/EditProject!default.jspa?pid=10000
	 * https://jira.pccwglobal.com/secure/admin/ConfigureFieldLayout!default.jspa?id=10100
	 * https://jira.pccwglobal.com/servicedesk/admin/UT2/sla/custom/4
	 */
	private static final PatternCheck[] PATTERNS = new PatternCheck[] {
		new PatternCheck(new URLPattern().setPath("/secure/RapidBoard.jspa").setQuery("rapidView")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				// TODO: projectid is all null in searchrequest table
				// So we cannot determine a filter's project directly
				// Have to parse the JQL?
				String sql = 
						"SELECT p.pkey FROM AO_60DB71_RAPIDVIEW rv " + 
						"JOIN searchrequest sr ON sr.ID = rv.SAVED_FILTER_ID " + 
						"JOIN project p ON p.id = sr.projectid";
				String boardId = getQueryItem(uri, "rapidView");
				return checkProjectKey(config, sql, 1, boardId);
			}			
		},
		new PatternCheck(new URLPattern().setPath("/secure/RapidBoard.jspa").setQuery("projectKey")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectKey = getQueryItem(uri, "projectKey");
				if (config.getIgnoreProjectKeys().contains(projectKey)) {
					return true;
				}
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPathRegex("/browse/([^/?]+).*")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String item = getPathItem(uri, 1);
				// Item can be either project key or issue key
				if (config.getIgnoreProjectKeys().contains(item)) {
					return true;
				}
				String sql = 
						"SELECT p.pkey FROM jiraissue i " + 
						"JOIN project p ON i.PROJECT = p.ID " + 
						"WHERE CONCAT(p.pkey, '-', issuenum) = ?";
				return checkProjectKey(config, sql, 1, item);
			}
		},
		new PatternCheck(new URLPattern().setPathRegex("/issues/([^?]+)").setQuery("jql")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				// TODO Parse JQL?
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPath("/issues").setQuery("jql")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				// TODO Parse JQL?
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPathRegex("/projects/([^/]+).*")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectKey = getPathItem(uri, 1);
				if (config.getIgnoreProjectKeys().contains(projectKey)) {
					return true;
				}
				return false;
			}
		},
		new PatternCheck(new URLPattern()
				.setPathRegex("/secure/CreateIssue(?:!default)?\\.jspa")
				.setQuery("pid")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectId = getQueryItem(uri, "pid");
				return checkProjectId(projectId);
			}
		},
		new PatternCheck(new URLPattern().setPath("/secure/Dashboard.jspa").setQuery("pageId")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String pageId = getQueryItem(uri, "pageId");
				// TODO Dashboard to project association is not well defined
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPath("/secure/Dashboard.jspa").setQuery("selectPageId")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String pageId = getQueryItem(uri, "selectPageId");
				// TODO Dashboard to project association is not well defined
				return false;
			}
		},
		new PatternCheck(new URLPattern()
				.setPathRegex("/secure/DataplaneReport(?:!default)?.jspa")
				.setQuery("report")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String reportId = getQueryItem(uri, "report");
				// TODO Association with project?
				return false;
			}
		},
		new PatternCheck(new URLPattern()
				.setPath("/secure/EditFilter(?:!default)?.jspa")
				.setQuery("filterId")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String filterId = getQueryItem(uri, "filterId");
				// TODO Parse filter JQL?
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPath("/secure/IssueNavigator.jspa").setQuery("jqlQuery")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String jql = getQueryItem(uri, "jqlQuery");
				// TODO Parse JQL?
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPath("/secure/PortfolioPlanView.jspa").setQuery("id")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String id = getQueryItem(uri, "id");
				// TODO Association to project?
				return false;
			}
		},
		new PatternCheck(new URLPattern().setPath("/secure/ReleaseNote.jspa").setQuery("projectId")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectId = getQueryItem(uri, "projectId");
				return checkProjectId(projectId);
			}
		},
		new PatternCheck(new URLPattern().setPathRegex("/secure/attachment/([0-9]+)/.+")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String attachmentId = getPathItem(uri, 1);
				String sql = 
						"SELECT p.pkey FROM FILEATTACHMENT a " + 
						"JOIN jiraissue i ON a.issueid = i.ID " + 
						"JOIN project p ON p.ID = i.PROJECT " + 
						"WHERE a.ID = ?";
				return checkProjectKey(config, sql, 1, attachmentId);
			}
		},
		new PatternCheck(new URLPattern().setPathRegex("/servicedesk/customer/portal/([0-9]+)")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String portalId = getPathItem(uri, 1);
				String sql = 
						"SELECT p.pkey FROM AO_54307E_VIEWPORT v " + 
						"JOIN project p ON p.ID = v.PROJECT_ID " + 
						"WHERE v.ID = ?";
				return checkProjectKey(config, sql, 1, portalId);
			}
		},
		new PatternCheck(new URLPattern()
				.setPathRegex("/secure/project/EditProject(?:!default)?\\.jspa").setQuery("pid")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectId = getQueryItem(uri, "pid");
				return checkProjectId(projectId);
			}
		},
		new PatternCheck(new URLPattern()
				.setPathRegex("/secure/admin/ConfigureFieldLayout(?:!default)?\\.jspa")
				.setQuery("id")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String id = getQueryItem(uri, "id");
				String sql = 
						"SELECT p.pkey FROM fieldlayout fl " + 
						"JOIN fieldlayoutschemeentity flse ON flse.FIELDLAYOUT = fl.ID " + 
						"JOIN fieldlayoutscheme fls ON fls.ID = flse.SCHEME " + 
						"JOIN fieldlayoutschemeassociation flsa ON flsa.FIELDLAYOUTSCHEME = fls.ID " + 
						"JOIN project p ON p.ID = flsa.PROJECT " + 
						"WHERE fl.ID = ?";
				return checkProjectKey(config, sql, 1, id);
			}
		},
		new PatternCheck(new URLPattern()
				.setPathRegex("/servicedesk/admin/([^/]+)/sla/custom/[0-9]+")) {
			@Override
			public boolean accept(Config config, URI uri) 
					throws SQLException {
				String projectKey = getPathItem(uri, 1);
				return config.getIgnoreProjectKeys().contains(projectKey);
			}
		},
	};
	
	@Override
	protected boolean _accept(URI uri) {
		Log.debug(LOGGER, "Checking URI: " + uri.toString());
		for (PatternCheck pc : PATTERNS) {
			Log.debug(LOGGER, "Checking pattern: " + pc.getURLPattern().getPathPattern().pattern());
			try {
				if (pc.getURLPattern().match(uri)) {
					if (pc.accept(config, uri)) {
						return true;
					}
				}
			} catch (SQLException e) {
				Log.error(LOGGER, "Error checking pattern", e);
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult handle(URI uri, String text) throws Exception {
		// Return URL unchanged
		return new HandlerResult(uri);
	}

}
