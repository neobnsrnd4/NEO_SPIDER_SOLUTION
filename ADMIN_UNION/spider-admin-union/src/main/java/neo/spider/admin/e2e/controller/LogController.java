package neo.spider.admin.e2e.controller;

import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import neo.spider.admin.e2e.dto.LogDTO;
import neo.spider.admin.e2e.service.LogService;
import neo.spider.admin.e2e.service.TraceUmlService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/e2e")
public class LogController {

	private final LogService logService;

	@GetMapping("/delay/request")
    public String findDelayRequestLogs(
			Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		// 초 단위를 밀리 초 단위로 변경
		String executeResult = paramDto.getExecuteResult();
        if(executeResult != null && !executeResult.isEmpty()){
			paramDto.setSearchExecuteTime((long) (Double.parseDouble(executeResult) * 1000));
        }
		if (paramDto.getStartTime() != null) {
			paramDto.setLtTimestamp(
					paramDto.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		if (paramDto.getEndTime() != null) {
			paramDto.setGtTimestamp(
					paramDto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}

		List<LogDTO> logList = logService.findDelayRequestLogs(paramDto, page, size);
		int totalLogs = logService.countDelayRequestLogs(paramDto);
		int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "e2e/delay_request_table";
    }

	@GetMapping("/delay/query")
	public String findDelayQueryLogs(
			Model model,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		// 초 단위를 밀리 초 단위로 변경
		String executeResult = paramDto.getExecuteResult();
		if(executeResult != null && !executeResult.isEmpty()){
			paramDto.setSearchExecuteTime((long) (Double.parseDouble(executeResult) * 1000));
		}
		if (paramDto.getStartTime() != null) {
			paramDto.setLtTimestamp(
					paramDto.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		if (paramDto.getEndTime() != null) {
			paramDto.setGtTimestamp(
					paramDto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}

		List<LogDTO> logList = logService.findDelayQueryLogs(paramDto, page, size);
		int totalLogs = logService.countDelayQueryLogs(paramDto);
		int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

		model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		return "e2e/delay_query_table";
	}
	
	@GetMapping("/errors")
	public String findErrorByPage(
			Model model,
			@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		if (paramDto.getStartTime() != null) {
			paramDto.setLtTimestamp(
					paramDto.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		if (paramDto.getEndTime() != null) {
			paramDto.setGtTimestamp(
					paramDto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}

        List<LogDTO> logList = logService.findErrorLogs(paramDto, page, size);
        int totalLogs = logService.countErrorLogs(paramDto);
        int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "e2e/error_table";
	}
	
	@GetMapping("/trace")
	public String findByTraceId(Model model, @RequestParam String traceId) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTraceId(traceId);

		model.addAttribute("logList", logList);
		model.addAttribute("imgSource", TraceUmlService.buildUmlList(logList));
		return "e2e/trace_table";
	}
	
	@GetMapping("/influence")
	public String findByTable(
			Model model,
			@RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {
		List<LogDTO> logList = logService.findInfluenceLogs(page, size, searchType, searchKeyword);
		int totalLogs = logService.countInfluenceLogs(searchType, searchKeyword);
	    int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

		model.addAttribute("logList", logList);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("searchType" , searchType);
	    model.addAttribute("searchKeyword", searchKeyword);
		
		return "e2e/influence_table";
	}
}
