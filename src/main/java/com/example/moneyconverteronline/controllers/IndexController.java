package com.example.moneyconverteronline.controllers;

import com.example.moneyconverteronline.models.ValutaModel;
import com.example.moneyconverteronline.services.MoneyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

@Controller
public class IndexController {
    MoneyService moneyService = new MoneyService();
    String amount;
    String ccode;
    String value;
    int count = 0;

    public IndexController() throws IOException {
    }


@GetMapping("/")
public String index(Model allCurrenciesForView){
    allCurrenciesForView.addAttribute("currcalc", value);
        return "index";
}


    @PostMapping("/submit")
        public String enterRate(WebRequest dataFromForm, Model allCurrenciesForView ) throws SQLException, IOException {
        ccode = dataFromForm.getParameter("ccode");
        amount = dataFromForm.getParameter("amount");
        moneyService.connect2Database();
        if (count == 0) {
            moneyService.csvValutaWriterToFile();
            count++;
        }
        return "redirect:/display";
    }

    @GetMapping("/display")
    public String getMoney(Model allCurrenciesForView) throws SQLException, FileNotFoundException {
        moneyService.convertMoneyViaCsv(ccode,amount);
        value = moneyService.getCalculatedValue();
        return "redirect:/";
    }
}
