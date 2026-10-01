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

    // Display login page
    @GetMapping("/")
    public String loginPage(
            @RequestParam(required = false) Boolean registered,
            Model model) {

        /*
         * If the user has just completed registration,
         * display a success message on the login page.
         */
        if (Boolean.TRUE.equals(registered)) {

            model.addAttribute(
                    "success",
                    "Account created successfully! You can now log in."
            );
        }

        return "login";
    }

    // Display login page when /login is opened directly
    @GetMapping("/login")
    public String loginPageWithLoginUrl() {

        return "login";
    }

    // Process login
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

            /*
             * Store customer information in the session.
             */
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

        /*
         * Login failed.
         */
        model.addAttribute(
                "error",
                "Invalid email or password."
        );

        return "login";
    }
}