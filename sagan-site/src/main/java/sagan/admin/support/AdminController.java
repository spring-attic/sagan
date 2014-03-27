package sagan.admin.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class AdminController {

    @RequestMapping("/admin")
    public String adminPage() {
        return "admin/show";
    }
}
