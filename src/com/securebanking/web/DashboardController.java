package com.securebanking.web;

import com.securebanking.AccountSummary;
import com.securebanking.CustomerAccountService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        int customerId =
                (Integer) customerIdObject;

        String email =
                (String) session.getAttribute("email");

        CustomerAccountService accountService =
                new CustomerAccountService();

        AccountSummary account =
                accountService.getCustomerAccount(
                        customerId
                );

        model.addAttribute(
                "customerId",
                customerId
        );

        model.addAttribute(
                "email",
                email
        );

        model.addAttribute(
                "account",
                account
        );

        return "dashboard";
    }
}
