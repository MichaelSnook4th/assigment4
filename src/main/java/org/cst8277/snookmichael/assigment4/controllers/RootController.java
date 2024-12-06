package org.cst8277.snookmichael.assigment4.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String handleRoot(HttpSession session) {
        // Redirect to login if user is not signed in
        if (session.getAttribute("name") == null || session.getAttribute("email") == null) {
            return "redirect:/login";
        }
        return "redirect:/home"; // Redirect to home if user is signed in
    }
}
