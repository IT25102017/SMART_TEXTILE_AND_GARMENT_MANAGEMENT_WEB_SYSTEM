package com.lankatex.smarttextile.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    // Display the LankaTex home page
    @GetMapping({"/", "/home"})
    public String home(Model model, Principal principal) {

        // Check whether a user is currently logged in
        model.addAttribute("loggedIn", principal != null);

        return "home";
    }

    // Display the custom LankaTex login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}