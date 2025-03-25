package neo.spider.solution.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import neo.spider.solution.dto.AdminApiDTO;
import neo.spider.solution.service.AdminService;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;
	
	@GetMapping("/admin/mock")
	public String showAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute AdminApiDTO paramDto,
            Model model) {

		 // 검색 및 페이징 처리된 API 목록 가져오기
        List<AdminApiDTO> apiList = adminService.findApisByPage(page, size, paramDto);
        int totalApis = adminService.countApis(paramDto);
        int totalPages = (int) Math.ceil((double) totalApis / size);

        // Model에 데이터 추가
        model.addAttribute("apiList", apiList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("param", paramDto); // 검색어 유지
		return "mock/api_list";
	}
	
}
