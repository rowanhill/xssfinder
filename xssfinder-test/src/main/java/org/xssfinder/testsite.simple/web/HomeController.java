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
    private static final List<String> strings = new ArrayList<String>();

    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView handleGet() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("strings", strings);
        return modelAndView;
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String handlePost(HttpServletRequest request) {
        strings.add(request.getParameter("textinput"));
        return "redirect:/simple/";
    }
}
