package neo.spider.solution.flowcontrol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.IntStream;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("path", "/test");
        return "test";
    }

    @GetMapping("/wait")
    public String testWait(Model model) throws InterruptedException {
        Thread.sleep(5000);
        model.addAttribute("path", "/wait");
        return "test";
    }

    @GetMapping("/wait/deep")
    public String testWaitDeep(Model model) throws InterruptedException {
        Thread.sleep(5000);
        model.addAttribute("path", "/wait/deep");
        return "test";
    }

    @GetMapping("/test/inside")
    public String testInside(Model model) {
        model.addAttribute("path", "/test/inside");
        int result = IntStream.rangeClosed(1, 10000).map(i -> i*i).sum();
        if (result % 2 == 0){
            System.out.println("even");
        } else { System.out.println("odd"); }
        return "test";
    }

    @GetMapping("/plus")
    public String testPlus(Model model) {
        model.addAttribute("path", "/plus");
        return "test";
    }


    @GetMapping("/product")
    public String testProduct(Model model) {
        model.addAttribute("path", "/product");
        return "test";
    }

    @GetMapping("/product/detail")
    public String testProductDetail(Model model, @RequestParam(name = "id", required = false, defaultValue = "6") String id) {
        model.addAttribute("path", "/product/detail");
        model.addAttribute("id", id);
        return "product-detail";
    }

    @GetMapping("/product/detail/dp")
    public String testProductDetailDP(Model model) {
        model.addAttribute("path", "/product/detail/dp");
        return "test";
    }

    @GetMapping("/product/add")
    public String testProductAdd(Model model) {
        model.addAttribute("path", "/product/add");
        return "test";
    }

    @GetMapping("/product/remove")
    public String testProductRemove(Model model) {
        model.addAttribute("path", "/product/remove");
        return "test";
    }

    @GetMapping("/product/update")
    public String testProductUpdate(Model model) {
        model.addAttribute("path", "/product/update");
        return "test";
    }
}
