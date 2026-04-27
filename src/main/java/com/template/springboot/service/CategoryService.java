package com.template.springboot.service;

import com.template.springboot.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto create(CategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);
}
