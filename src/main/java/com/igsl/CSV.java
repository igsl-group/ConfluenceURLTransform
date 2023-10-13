package com.igsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

/**
 * Wrapper for CSVPrinter to sanitize CSV data.
 * 
 * Make sure data does not start with:
 * =
 * +
 * -
 * @
 * Tab
 * Newline
 * 
 * Data is always surrounded by double quotes.
 * Double quotes in data are doubled. 
 * 
 * Reference: 
 * https://owasp.org/www-community/attacks/CSV_Injection
 */
public class CSV {
	private static final Pattern INVALID_START_CHAR = Pattern.compile("^[=+\\-@\\t\\r\\n]+(.*)");
	
	public static CSVFormat getCSVReadFormat() {
		CSVFormat fmt = CSVFormat.EXCEL;
		return fmt.builder()
			.setDelimiter(",")
			.setQuoteMode(QuoteMode.ALL)
			.setHeader()
			.build();
	}
	
	public static CSVFormat getCSVWriteFormat(List<String> headers) {
		CSVFormat fmt = CSVFormat.EXCEL;
		return fmt.builder()
			.setDelimiter(",")
			.setQuoteMode(QuoteMode.ALL)
			.setHeader(headers.toArray(new String[0]))
			.build();
	}
	
	public static void printRecord(CSVPrinter printer, List<?> args) throws IOException {
		printer.printRecord(args.toArray(new Object[0]));
	}
	
	public static void printRecord(CSVPrinter printer, Object... args) throws IOException {
		List<Object> newArgs = new ArrayList<>();
		for (Object o : args) {
			if (o != null && o instanceof String) {
				String s = (String) o;
				Matcher m = INVALID_START_CHAR.matcher(s);
				if (m.matches()) {
					s = m.group(1);
				} 
				newArgs.add(s);
			} else {
				newArgs.add(o);
			}
		}
		printer.printRecord(newArgs.toArray(new Object[0]));
	}
	
}
