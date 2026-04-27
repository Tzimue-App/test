package com.template.springboot.controller;

import com.template.springboot.dto.ItemDto;
import com.template.springboot.service.CategoryService;
import com.template.springboot.service.ItemService;
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
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final int PAGE_SIZE = 10;
    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) Long categoryId,
                       Model model) {
        var pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        model.addAttribute("page", categoryId != null
                ? itemService.findByCategory(categoryId, pageable)
                : itemService.findAll(pageable));
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", categoryService.findAll(PageRequest.of(0, 100)));
        return "item/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("itemDto",    new ItemDto());
        model.addAttribute("categories", categoryService.findAll(PageRequest.of(0, 100)));
        return "item/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ItemDto itemDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll(PageRequest.of(0, 100)));
            return "item/form";
        }
        itemService.create(itemDto);
        ra.addFlashAttribute("successMsg", "Item created successfully.");
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("itemDto",    itemService.findById(id));
        model.addAttribute("categories", categoryService.findAll(PageRequest.of(0, 100)));
        return "item/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute ItemDto itemDto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll(PageRequest.of(0, 100)));
            return "item/form";
        }
        itemService.update(id, itemDto);
        ra.addFlashAttribute("successMsg", "Item updated successfully.");
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        itemService.delete(id);
        ra.addFlashAttribute("successMsg", "Item deleted.");
        return "redirect:/items";
    }
}
