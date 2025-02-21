package neo.spider.admin.e2e.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import neo.spider.admin.e2e.dto.LogDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TraceUmlService {

	public static String buildUmlList(List<LogDTO> logList) {
		List<UmlDTO> uList = new ArrayList<UmlDTO>();
		for (LogDTO log : logList) {
			UmlDTO uml = new UmlDTO();

			uml.setBefore(formatUser(log.getCallerComponentName()));
			uml.setTo(formatUser(log.getTargetComponentName()));
			uml.setContent(formatContent(log));
			uml.setColor(formatColor(log));

			uList.add(uml);
		}
		return buildUmlDiagram(uList);
	}

	public static String buildUmlDiagram(List<UmlDTO> umList) {
		StringBuilder sb = new StringBuilder("autonumber\n");
		sb.append("actor USER\n").append("skinparam sequenceArrowThickness 2\n").append("skinparam roundcorner 20\n");

		for (UmlDTO uml : umList) {
			sb.append(String.format("%s %s %s : <font color=%s> %s\n", formatForAppend(uml.getBefore()), "->",
					formatForAppend(uml.getTo()), uml.getColor(), uml.getContent()));
		}
		return sb.toString();
	}

	// _---------------------------------- uml 그리기 메서드
	// -------------------------------

	private static String formatUser(String s) {
		if (s.startsWith("http://") || s.startsWith("https://")) {
			try {
				URL url = new URL(s);
				return url.getHost() + ":" + url.getPort();
			} catch (MalformedURLException e) {
				return s;
			}
		} else if (s.equals("USER")) {
			return s;
		} else {
			String[] parts = s.split("\\.");
			return parts[parts.length - 2];
		}
	}

	private static String formatContent(LogDTO log) {
		if (log.getExecutionTime() != null) {
			String res = "";
			if (log.getQuery() != null) {
				res += "[" + log.getQuery() + "]";
			}
			if(log.getDelayMessageText() != null) {
				res += " [" + log.getDelayMessageText() + "]";
			}
			return res + " [" + log.getExecutionTime().toString() + "ms]";
		} else {
			String s = log.getTargetComponentName();
			if (s.startsWith("http://") || s.startsWith("https://")) {
				try {
					URL url = new URL(s);
					return url.getPath();
				} catch (MalformedURLException e) {
					return s;
				}
			} else {
				String[] parts = s.split("\\.");
				return parts[parts.length - 1];
			}
		}
	}

	private static String formatColor(LogDTO log) {
		if (log.getErrorMessageText() != null && !log.getErrorMessageText().isEmpty()) {
			return "red";
		}
		// delay가 존재하면 orangered
		if (log.getDelayMessageText() != null && !log.getDelayMessageText().isEmpty()) {
			return "orangered";
		}
		// 기본 색상은 black
		return "black";
	}

	private static String formatForAppend(String s) {
		if (s.contains(":"))
			return "\"" + s + "\"";
		return s;
	}
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class UmlDTO {
	String before;
	String to;
	String color;
	String content;
}