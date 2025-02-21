package neo.spider.admin.common.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import neo.spider.admin.common.dto.FwkUserDTO;
import neo.spider.admin.common.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@RequestMapping(value = "/registerUser", method = { RequestMethod.GET, RequestMethod.POST })
	public String registerUser(@ModelAttribute @Valid FwkUserDTO fwkUserDTO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("errors", result.getAllErrors());
			model.addAttribute("user", fwkUserDTO);
			return "signup"; // 폼 화면으로 다시 이동
		}

		fwkUserDTO.setUserId(fwkUserDTO.getUsername());
		fwkUserDTO.setUserSsn(fwkUserDTO.getUserSsn().replaceAll("-", ""));

		authService.insertUser(fwkUserDTO);
		model.addAttribute("errMsg", "등록한 신규사용자로 로그인하세요.");
		// 성공 처리
		model.addAttribute("successMessage", "User registered successfully!");
		return "signin";
	}

	@GetMapping("/")
	public String index(Model model) {
		return "index_page";
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public String logout(Model model, HttpServletRequest request) {

		request.getSession().invalidate();
		return "signin";
	}

	@GetMapping("/signin")
	public String login(@RequestParam(required = false) String username,
			@RequestParam(required = false) String password, Model model, HttpServletRequest request) {
		String errMsg = (String) request.getSession().getAttribute("errMsg");
		model.addAttribute("errMsg", errMsg);
		request.getSession().invalidate();
		List<FwkUserDTO> user = authService.findSById("username");
		return "signin";
	}

	@GetMapping("/signup")
	public String signup(@RequestParam(required = false) String username,
			@RequestParam(required = false) String password, Model model, HttpServletRequest request) {
		request.getSession().invalidate();
		List<FwkUserDTO> user = authService.findSById("username");

		model.addAttribute("user", new FwkUserDTO());
		return "signup";
	}

	@GetMapping("/indexPage")
	public String indexPage(@RequestParam(required = false) String username,
			@RequestParam(required = false) String password, Model model, HttpServletRequest request) {
		return "index_page";
	}

	@RequestMapping(value = "/loginProcess", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginProcess(@RequestParam(required = false) String username,
			@RequestParam(required = false) String password, Model model, HttpServletRequest request) {

		List<FwkUserDTO> user = authService.findSById(username);

		FwkUserDTO loginUser = null;

//		// 임시 로그인 처리.
		HttpSession session = request.getSession();
		if (user == null || user.isEmpty()) {
			model.addAttribute("errMsg", "ID와 패스워드를 확인하세요.");
			session.invalidate();
			return "signin";
		} else {
			loginUser = user.get(0);
			if (!password.equals(loginUser.getPassword())) {
				model.addAttribute("errMsg", "ID와 패스워드를 확인하세요.");
				session.invalidate();
				return "signin";
			}
		}
//		
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formattedDate = now.format(formatter);
		authService.updateUserSession(username, session.getId(), formattedDate);

		session.setAttribute("userId", username);
		session.setAttribute("loginSessionId", session.getId());
		session.setAttribute("lgoinDtime", formattedDate);

//	       //세션 데이터 출력
//        session.getAttributeNames().asIterator()
//                .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));
//        System.out.println("sessionId={}"+ session.getId());
//        System.out.println("getMaxInactiveInterval={}"+ session.getMaxInactiveInterval());
//        System.out.println("creationTime={}"+ new Date(session.getCreationTime()));
//        System.out.println("lastAccessedTime={}"+ new Date(session.getLastAccessedTime()));
//        System.out.println("isNew={}"+ session.isNew());

		return "index_page";
	}
}
