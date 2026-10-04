package com.example.companies_list.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyDashboardController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "forward:/index.html";
    }
}
