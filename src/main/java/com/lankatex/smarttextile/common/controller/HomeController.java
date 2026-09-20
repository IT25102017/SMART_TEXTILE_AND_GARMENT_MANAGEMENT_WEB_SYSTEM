package com.lankatex.smarttextile.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Display the LankaTex home page
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    // Display the custom LankaTex login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}