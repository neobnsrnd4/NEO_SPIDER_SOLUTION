package neo.spider.admin.flow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.spider.admin.flow.dto.CreateApplicationDto;
import neo.spider.admin.flow.dto.SearchApplicationResultDto;
import neo.spider.admin.flow.dto.SearchDto;
import neo.spider.admin.flow.dto.UpdateApplicationDto;
import neo.spider.admin.flow.dto.bulkhead.BulkheadSearchDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterSearchDto;
import neo.spider.admin.flow.service.BulkheadService;
import neo.spider.admin.flow.service.ControlService;
import neo.spider.admin.flow.service.MessagePublisher;
import neo.spider.admin.flow.service.RateLimiterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/flow")
public class ControlController {

    private final ControlService controlService;
    private final RateLimiterService rateLimiterService;
    private final BulkheadService bulkheadService;
    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public ControlController(ControlService controlService, MessagePublisher messagePublisher, ObjectMapper objectMapper,
                             BulkheadService bulkheadService, RateLimiterService rateLimiterService) {
        this.controlService = controlService;
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
        this.bulkheadService = bulkheadService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/list")
    public String findPage(Model model,
                           @ModelAttribute SearchDto dto,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {

        try {
            if (dto.getApplicationId() != null) {
                int id = Integer.parseInt(dto.getApplicationId());
            }
        } catch (Exception e) {
            int totalPage = 0;
            dto.setApplicationId(null);
            model.addAttribute("results", new ArrayList<SearchApplicationResultDto>());
            model.addAttribute("page", page);
            model.addAttribute("totalPage", totalPage);
            model.addAttribute("size", size);
            model.addAttribute("param", dto);
            model.addAttribute("range", new int[]{});
        }
        int total = controlService.count(dto);
        List<SearchApplicationResultDto> results = controlService.find(dto, page, size);
        int totalPage = (total / size) + (total % size == 0 ? 0 : 1);
        int dix = (page / 10) * 10;
        int start = dix + 1;
        int end = dix + 10;
        end = Math.min(end, totalPage);
        int[] range = IntStream.range(start, end + 1).toArray();

        model.addAttribute("results", results);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("size", size);
        model.addAttribute("param", dto);
        model.addAttribute("range", range);

        return "flow/application_list";
    }

    @PostMapping("/createApplication")
    public String create(@ModelAttribute CreateApplicationDto dto) {
        int result = controlService.create(dto);
        if (result == 0) {
            System.out.println("create failed");
        } else {
            System.out.println("create success");
        }
        return "redirect:/admin/flow/list";
    }

    @GetMapping("/delete")
    public String delete(Model model,
                         @ModelAttribute SearchDto dto) {
        int result = controlService.delete(Long.parseLong(dto.getApplicationId()));
        if (result == 0) {
            System.out.println("delete failed");
        } else {
            System.out.println("delete success");
        }
        return "redirect:/admin/flow/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") long id,
                         @RequestParam(required = false) String bulkheadUrl,
                         @RequestParam(required = false, defaultValue = "-1") int ratelimiterType,
                         @RequestParam(required = false) String ratelimiterUrl) {
        SearchApplicationResultDto application = controlService.findById(id);
        List<BulkheadSearchDto> bulkheads = bulkheadService.findByApplication(application.getApplicationId(), bulkheadUrl);
        List<RateLimiterSearchDto> rateLimiters = rateLimiterService.findByApplication(application.getApplicationId(), ratelimiterType, ratelimiterUrl);
        model.addAttribute("app", application);
        model.addAttribute("bulkheads", bulkheads);
        model.addAttribute("bulkheadUrl", bulkheadUrl);
        model.addAttribute("rateLimiters", rateLimiters);
        model.addAttribute("ratelimiterType", ratelimiterType);
        model.addAttribute("ratelimiterUrl", ratelimiterUrl);
        return "flow/application_detail";
    }

    @PostMapping("/updateApplication")
    public String update(@ModelAttribute UpdateApplicationDto dto) {
        int result = controlService.update(dto);
        if (result == 0) {
            System.out.println("update failed");
        } else {
            System.out.println("update success");
        }
        return "redirect:/admin/flow/detail/" + dto.getApplicationId();
    }

}
