package neo.spider.demo.E2E.dto;

import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class LogDTO {

	private String timestmp;
	private String loggerName;
	private String levelString;
	private String callerClass;
	private String callerMethod;
	private String query;
	private String uri;
	private String traceId;
	private String userId;
	private String ipAddress;
	private String device;
	private String executeResult;
	private String serverId = "BT20";
	private long lineNumber;

	// FWK_ERROR_HIS 로 매핑
	public String mapErrorCode() {

		String result = "";

		if (executeResult == null) {
			result = "BEE00001"; // backend etc
		} else if (callerClass.equals("SQL")) {

			if (executeResult.contains("IllegalArgumentException")) {
				result = "BET00001"; // backend transaction
			} else {
				result = "BET00002";
			}

		} else {

			result = "BEO00001"; // backend other

		}

		return result;

	}

	public String generateErrorSerNo() throws Exception {

		String formattedTime = convertTimestamp();

		return (formattedTime + serverId + traceId).substring(0,26);
	}

	public String convertTimestamp() throws Exception {

		String formattedTime = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timestmp));

		return formattedTime;
	}

	public String convertUrl() {

		String returnUrl = "";
		if (uri != null && uri.length()>=1 && uri.charAt(0)=='/') {
			returnUrl = uri.substring(1);
		}

		return returnUrl;
	}

}
