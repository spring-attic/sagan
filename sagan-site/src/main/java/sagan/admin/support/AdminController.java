package sagan.admin.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @RequestMapping("/admin")
    public String adminPage() {
        return "admin/show";
    }

    @RequestMapping("/admin/500")
    public void simulate500() {
        throw new RuntimeException("Simulated Exception");
    }
}
