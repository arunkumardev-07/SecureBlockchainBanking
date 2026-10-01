package com.securebanking.web;

import com.securebanking.CustomerAccountService;
import com.securebanking.FundTransferService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TransferController {

    @GetMapping("/transfer")
    public String transferPage(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String senderAccount,
            @RequestParam String receiverAccount,
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
                    "Transfer amount must be greater than zero."
            );

            return "transfer";
        }

        // Verify that the sender account
        // belongs to the logged-in customer
        CustomerAccountService accountService =
                new CustomerAccountService();

        boolean accountOwned =
                accountService.isAccountOwnedByCustomer(
                        customerId,
                        senderAccount
                );

        if (!accountOwned) {

            model.addAttribute(
                    "error",
                    "You are not authorized to use this sender account."
            );

            return "transfer";
        }

        // Call the existing transfer service
        FundTransferService transferService =
                new FundTransferService();

        transferService.transferMoney(
                senderAccount,
                receiverAccount,
                amount
        );

        return "redirect:/dashboard";
    }
}
