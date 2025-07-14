package com.example.simplezakka.controller;

import com.example.simplezakka.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/a-kanri")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "a-kanri"; // resources/templates/a-kanri.html を表示
    }
}

