package com.template.springboot.controller;

import com.template.springboot.service.CategoryService;
import com.template.springboot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentItems",   itemService.findAll(PageRequest.of(0, 5)));
        model.addAttribute("categories",    categoryService.findAll(PageRequest.of(0, 6)));
        return "index";
    }
}
