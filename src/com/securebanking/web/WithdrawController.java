package com.securebanking.web;

import com.securebanking.CustomerAccountService;
import com.securebanking.WithdrawService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WithdrawController {

    @GetMapping("/withdraw")
    public String withdrawPage(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(
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

        if (amount <= 0) {

            model.addAttribute(
                    "error",
                    "Withdrawal amount must be greater than zero."
            );

            return "withdraw";
        }

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

            return "withdraw";
        }

        WithdrawService withdrawService =
                new WithdrawService();

        withdrawService.withdrawMoney(
                accountNumber,
                amount
        );

        return "redirect:/dashboard";
    }
}
