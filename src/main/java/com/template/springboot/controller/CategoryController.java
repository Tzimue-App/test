package com.template.springboot.controller;

import com.template.springboot.dto.CategoryDto;
import com.template.springboot.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final int PAGE_SIZE = 10;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", categoryService.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("name"))));
        return "category/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        return "category/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CategoryDto categoryDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "category/form";
        }
        categoryService.create(categoryDto);
        ra.addFlashAttribute("successMsg", "Category created successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("categoryDto", categoryService.findById(id));
        return "category/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute CategoryDto categoryDto,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "category/form";
        }
        categoryService.update(id, categoryDto);
        ra.addFlashAttribute("successMsg", "Category updated successfully.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        categoryService.delete(id);
        ra.addFlashAttribute("successMsg", "Category deleted.");
        return "redirect:/categories";
    }
}
