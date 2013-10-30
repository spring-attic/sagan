package sagan.guides.web;

import sagan.guides.support.UnderstandingDocs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class UnderstandingDocController {

    private final UnderstandingDocs docs;

    @Autowired
    public UnderstandingDocController(UnderstandingDocs docs) {
        this.docs = docs;
    }

    @RequestMapping(value = "/understanding/{subject}", method = { GET, HEAD })
    public String viewSubject(@PathVariable String subject, Model model) {
        model.addAttribute("doc", docs.find(subject));
        return "understanding/show";
    }
}
