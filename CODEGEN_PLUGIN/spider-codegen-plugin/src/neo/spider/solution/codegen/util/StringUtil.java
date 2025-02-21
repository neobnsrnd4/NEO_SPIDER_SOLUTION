package neo.spider.solution.codegen.util;

public class StringUtil {
	public static String toPascalCase(String input) {
		// Split the input string by underscores
		String[] parts = input.split("_");

		// Initialize a StringBuilder for the result
		StringBuilder pascalCaseString = new StringBuilder();

		// Iterate through the parts
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			// Capitalize the first letter of the subsequent parts
			pascalCaseString.append(part.substring(0, 1).toUpperCase()); // 첫번째는 대문자
			pascalCaseString.append(part.substring(1));
		}

		return pascalCaseString.toString();
	}
}
