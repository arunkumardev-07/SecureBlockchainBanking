package com.securebanking.web;

import com.securebanking.CustomerAccountService;
import com.securebanking.DepositService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DepositController {

    @GetMapping("/deposit")
    public String depositPage(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam String accountNumber,
            @RequestParam double amount,
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        int customerId =
                (Integer) customerIdObject;

        // Validate amount
        if (amount <= 0) {

            model.addAttribute(
                    "error",
                    "Deposit amount must be greater than zero."
            );

            return "deposit";
        }

        // Verify that the account belongs
        // to the logged-in customer
        CustomerAccountService accountService =
                new CustomerAccountService();

        boolean accountOwned =
                accountService.isAccountOwnedByCustomer(
                        customerId,
                        accountNumber
                );

        if (!accountOwned) {

            model.addAttribute(
                    "error",
                    "You are not authorized to use this account."
            );

            return "deposit";
        }

        // Call the existing deposit service
        DepositService depositService =
                new DepositService();

        depositService.depositMoney(
                accountNumber,
                amount
        );

        return "redirect:/dashboard";
    }
}
