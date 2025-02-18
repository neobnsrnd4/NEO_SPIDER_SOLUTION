package neo.spider.solution.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Value("${admin.id}")
    private String adminId;

    @Value("${admin.password}")
    private String adminPassword;


    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; 
    }


    @PostMapping("/login")
    public String login(@RequestParam String id, 
                        @RequestParam String password, 
                        HttpSession session, Model model) {
        if (adminId.equals(id) && adminPassword.equals(password)) {
            session.setAttribute("adminUser", true); // 세션에 관리자 인증 정보 저장
            return "redirect:/admin/mock";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login"; 
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login"; 
    }

    @GetMapping("/indexPage")
    public String indexPage(@RequestParam(required = false) String username,
                            @RequestParam(required = false) String password, Model model, HttpServletRequest request) {
        return "index_page";
    }
}
