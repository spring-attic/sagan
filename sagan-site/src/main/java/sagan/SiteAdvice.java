package sagan;

import sagan.support.ViewHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller {@link org.springframework.web.bind.annotation.ControllerAdvice advice} for
 * all controllers of Sagan.
 */
@ControllerAdvice
public class SiteAdvice {

    @Autowired
    private ViewHelper viewHelper;

    /**
     * Get the {@link sagan.support.ViewHelper} used by the views.
     */
    @ModelAttribute("viewHelper")
    public ViewHelper viewHelper() {
        return viewHelper;
    }
}
