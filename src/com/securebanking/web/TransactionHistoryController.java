package com.securebanking.web;

import com.securebanking.TransactionHistoryService;
import com.securebanking.TransactionSummary;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TransactionHistoryController {

    @GetMapping("/transaction-history")
    public String transactionHistory(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        int customerId =
                (Integer) customerIdObject;

        TransactionHistoryService historyService =
                new TransactionHistoryService();

        List<TransactionSummary> transactions =
                historyService.getTransactions(
                        customerId
                );

        model.addAttribute(
                "transactions",
                transactions
        );

        return "transaction-history";
    }
}
