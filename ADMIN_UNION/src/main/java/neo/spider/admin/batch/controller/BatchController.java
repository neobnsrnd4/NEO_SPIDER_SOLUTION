package neo.spider.admin.batch.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import neo.spider.admin.batch.dto.BatchJobExecutionDTO;
import neo.spider.admin.batch.dto.BatchJobInstanceDTO;
import neo.spider.admin.batch.dto.BatchStepExecutionDTO;
import neo.spider.admin.batch.service.BatchService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/batch")
public class BatchController {

	private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

	private final BatchService batchService;

	@GetMapping("/jobList")
	public String list(Model model,
					   @RequestParam(defaultValue = "1") int page,
					   @RequestParam(defaultValue = "10") int size,
					   @ModelAttribute BatchJobInstanceDTO paramDto) {
		logger.info("{}", size);
		List<BatchJobInstanceDTO> jobList = batchService.findJobs(paramDto, page, size);
		logger.info("jobList size : {}", jobList.size());
		int totalJobs = batchService.countJobs(paramDto);
		logger.info("totalJobs : {}", totalJobs);
		int totalPages = totalJobs == 0 ? 0 : (int) Math.ceil((double) totalJobs / size);
		String[] status = { "COMPLETED", "STARTING", "STARTED", "STOPPING", "STOPPED", "FAILED", "UNKNOWN" };

		model.addAttribute("jobList", jobList);
		model.addAttribute("param", paramDto);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("statusList", status);

		return "batch/job_list";
	}

	@GetMapping("/jobDetail")
	public String jobDetail(Model model, BatchJobInstanceDTO paramDto) {

		BatchJobInstanceDTO job = null;
		List<BatchStepExecutionDTO> steps = null;

		job = batchService.findJobById(paramDto.getInstanceId(), paramDto.getExecutionId());

		logger.info("BatchJobInstanceEntity : " + job.toString());

		if (job.getExec() != null) {
			steps = batchService.findStepsByJobId(job.getExecutionId());
		}

		model.addAttribute("job", job);
		model.addAttribute("steps", steps);

		return "batch/job_detail";
	}

	@GetMapping("/stepDetail")
	public String stepDetail(Model model, BatchJobExecutionDTO paramDto) {

		BatchJobExecutionDTO job = batchService.findStepById(paramDto.getExecutionId());

		logger.info("BatchJobExecutionEntity : " + job.toString());

		model.addAttribute("job", job);
		return "batch/step_detail";
	}

}