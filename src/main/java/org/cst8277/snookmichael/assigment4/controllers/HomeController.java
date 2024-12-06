package org.cst8277.snookmichael.assigment4.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Debug session attributes
        System.out.println("Name: " + session.getAttribute("name"));
        System.out.println("Email: " + session.getAttribute("email"));

        // Add attributes to the model
        model.addAttribute("name", session.getAttribute("name"));
        model.addAttribute("email", session.getAttribute("email"));

        return "home";
    }
}
