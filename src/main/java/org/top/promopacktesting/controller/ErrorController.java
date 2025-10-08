package org.top.promopacktesting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDeniedGet() {
        return "access-denied";
    }

    @PostMapping("/access-denied")
    public String accessDeniedPost() {
        return "access-denied";
    }
}
