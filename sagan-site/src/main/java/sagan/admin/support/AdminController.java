package sagan.admin.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles requests for the root admin page. Administrative operations for
 * /blog, /team and /projects subsections are handled by their respective controllers.
 * 
 * @see sagan.blog.support.BlogAdminController
 * @see sagan.team.support.TeamAdminController
 * @see sagan.projects.support.ProjectAdminController
 */
@Controller
class AdminController {

    @RequestMapping("/admin")
    public String adminPage() {
        return "admin/show";
    }
}
