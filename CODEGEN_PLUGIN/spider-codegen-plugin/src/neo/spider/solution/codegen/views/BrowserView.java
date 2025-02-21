package neo.spider.solution.codegen.views;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import jakarta.inject.Inject;
import neo.spider.solution.codegen.common.DatabaseConnector;
import neo.spider.solution.codegen.common.MyBatisGeneratorProgrammatic;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view with html and javascript content. The view 
 * shows how data can be exchanged between Java and JavaScript.
 */

public class BrowserView extends ViewPart  {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "neo.spider.solution.codegen.views.BrowserView";

	@Inject
	Shell shell;


	private Browser fBrowser;

	@Override
	public void createPartControl(Composite parent) {
		fBrowser = new Browser(parent, SWT.EDGE);
		fBrowser.setText(getContent());
		BrowserFunction prefs = new ListTables(fBrowser, "invokeListTables"); // js에서 invokeListTables를 호출하면 ListTables 메소드가 호출됨
		fBrowser.addDisposeListener(e -> prefs.dispose());
		BrowserFunction codeGen = new GenerateCode(fBrowser, "invokeGenerateCode"); // js에서 invokeGenerateCode를 호출하면 GenerateCode 메소드가 호출됨
		fBrowser.addDisposeListener(e -> codeGen.dispose());
	}

	@Override
	public void setFocus() {
		fBrowser.setFocus();
	}

	private class ListTables extends BrowserFunction { // 데이터베이스 테이블 목록 가져오기

		ListTables(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			try {
				return DatabaseConnector.getTablesInfo(arguments[0].toString(), arguments[1].toString(), arguments[2].toString()).toString();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private class GenerateCode extends BrowserFunction { // 입력한 데이터를 기반으로 코드 생성

		GenerateCode(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			return MyBatisGeneratorProgrammatic.execute(shell, arguments[0].toString(), arguments[1].toString(),
					arguments[2].toString(), arguments[3].toString(), arguments[4].toString());
		}
	}
	
	public String getContent() {
		String js = null;
		try (InputStream inputStream = getClass().getResourceAsStream("BrowserView.js")) {
			js = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<!doctype html>")
			.append("<html lang=\"en\">")
			.append("<head>")
			.append("<meta charset=\"utf-8\">")
			.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
			.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\"")
			.append(" rel=\"stylesheet\" crossorigin=\"anonymous\"")
			.append(" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\">")
			.append("<title>Spider CodeGen</title>")
			.append("<script>" + js + "</script>") // 자바스크립트 내용을 추가
			.append("</head>")
			.append("<body class=\"p-3\">")
			.append("<form>")
			.append("<div class=\"row align-items-center\"><label class=\"col col-form-label\" style=\"min-width: 144px; max-width: 144px; font-size: .875rem;\">데이터베이스 URL:</label>")
			.append("<div class=\"col\"><input class=\"form-control form-control-sm\" type=\"text\" id=\"dbUrl\" name=\"dbUrl\" placeholder=\"ex) jdbc:mysql://localhost:3306/db2\"></div></div>")
			.append("<div class=\"row align-items-center\"><label class=\"col col-form-label\" style=\"min-width: 144px; max-width: 144px; font-size: .875rem;\">사용자명:</label>")
			.append("<div class=\"col\"><input class=\"form-control form-control-sm\" type=\"text\" id=\"username\" name=\"username\" placeholder=\"ex) root\"></div></div>")
			.append("<div class=\"row align-items-center\"><label class=\"col col-form-label\" style=\"min-width: 144px; max-width: 144px; font-size: .875rem;\">비밀번호:</label>")
			.append("<div class=\"col\"><input class=\"form-control form-control-sm\" type=\"password\" id=\"password\" name=\"password\" placeholder=\"ex) 1234\"></div></div>")
			.append("<div class=\"row align-items-center\"><label class=\"col col-form-label\" style=\"min-width: 144px; max-width: 144px; font-size: .875rem;\">저장할 폴더명:</label>")
			.append("<div class=\"col\"><input class=\"form-control form-control-sm\" type=\"text\" id=\"targetPath\" name=\"targetPath\" placeholder=\"ex) example\"></div></div>")
			.append("</form>")
			.append("<div class=\"d-flex justify-content-end\"><input class=\"btn btn-primary btn-sm\" type=\"button\" value=\"전체 테이블 조회\" onclick=\"listTables();\"></div>")
			.append("<div class=\"my-4\" id=\"tables\"></div>")
			.append("<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\" crossorigin=\"anonymous\"")
			.append(" integrity=\"sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz\"></script>")
			.append("</body>")
			.append("</html>");
		
		return sb.toString();
	}

}
