package neo.spider.admin.common.interceptor;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import neo.spider.admin.common.dto.FwkUserDTO;
import neo.spider.admin.common.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {
	private final AuthService authService;

	// 생성자 주입
	public SessionInterceptor(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		HttpSession session = request.getSession();

		// 세션에 값 설정 (예: 사용자 정보를 추가)

		String loginSessionId = (String) session.getAttribute("loginSessionId");

		if (loginSessionId == null || loginSessionId.equals("")) {
			session.setAttribute("errMsg", "재 로그인해 주세요.");
			response.sendRedirect("/signin");
			return false; // 리다이렉트 후 더 이상 컨트롤러로 진행되지 않음
		}

		if (session.getAttribute("userId") == null || ((String) session.getAttribute("userId")).equals("")) {
			session.setAttribute("errMsg", "재 로그인해 주세요.");
			response.sendRedirect("/signin");
			return false; // 리다이렉트 후 더 이상 컨트롤러로 진행되지 않음
		}

		String loginId = (String) session.getAttribute("userId");
		List<FwkUserDTO> users = authService.findSById(loginId);
		FwkUserDTO user = null;
		if (users == null || users.size() == 0) {
			session.setAttribute("errMsg", "재 로그인해 주세요.");
			response.sendRedirect("/signin");
			return false;
		}

		user = users.get(0);
		long lastLoginDtime = Long.parseLong(user.getLastUpdateDtime());
		long currentLoginDtime = 0;
		try {
			currentLoginDtime = Long.parseLong((String) session.getAttribute("lgoinDtime"));
		} catch (Exception e) {
			currentLoginDtime = 0;
		}

		if (lastLoginDtime > currentLoginDtime) {
			session.setAttribute("LOGIN_YN", "N");
			session.setAttribute("errMsg", "재 로그인해 주세요.");
			response.sendRedirect("/signin");
			return false;
		} else {
			session.setAttribute("LOGIN_YN", "Y");
		}

		return true; // true를 반환하면 다음으로 진행 (컨트롤러 실행), false면 요청 중단
	}
}
