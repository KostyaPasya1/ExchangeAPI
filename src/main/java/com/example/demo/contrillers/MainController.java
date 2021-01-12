package com.example.demo.contrillers;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

@Controller
public class MainController {


    @GetMapping("/")
    public String greeting (Model model) {

        //Получаем залогиненного пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();

        model.addAttribute("authName", authName);
        return "greetingPage";
    }




}