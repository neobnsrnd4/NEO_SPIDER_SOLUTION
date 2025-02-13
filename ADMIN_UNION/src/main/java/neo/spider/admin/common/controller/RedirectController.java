package neo.spider.admin.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rrr")
public class RedirectController {
    @GetMapping
    public String handleRequest(@RequestParam(required = false) String a) {
    	//a = a.replace(".html","");
        return "redirect:/" + a;
    }
}
