package com.securebanking.web;

import com.securebanking.LoginService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/login")
    public String loginPageWithLoginUrl() {

        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        LoginService loginService =
                new LoginService();

        boolean loginSuccessful =
                loginService.login(
                        email,
                        password
                );

        if (loginSuccessful) {

            int customerId =
                    loginService.getLoggedInCustomerId();

            // Store logged-in customer information
            // in the browser session
            session.setAttribute(
                    "customerId",
                    customerId
            );

            session.setAttribute(
                    "email",
                    email
            );

            return "redirect:/dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid email or password."
        );

        return "login";
    }
}