package org.xssfinder.testsite.simple.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final List<String> unsafeStrings = new ArrayList<String>();
    private static final List<String> safeStrings = new ArrayList<String>();

    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView handleGet() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("unsafeStrings", unsafeStrings);
        modelAndView.addObject("safeStrings", safeStrings);
        return modelAndView;
    }

    @RequestMapping(value="/unsafe", method=RequestMethod.POST)
    public String handlePost(HttpServletRequest request) {
        unsafeStrings.add(request.getParameter("textinput"));
        return "redirect:/simple/";
    }

    @RequestMapping(value="/safe", method=RequestMethod.POST)
    public String handleSafePost(HttpServletRequest request) {
        safeStrings.add(request.getParameter("textinput"));
        return "redirect:/simple/";
    }
}
