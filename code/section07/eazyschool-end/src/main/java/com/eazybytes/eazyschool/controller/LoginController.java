package com.eazybytes.eazyschool.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public String displayLoginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model) {
        String errorMessage = null;
        if(null != error) {
            errorMessage = "Username or Password is incorrect !!";
        }
        if(null!= logout) {
            errorMessage = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessage", errorMessage);
        return "login.html";
    }

}