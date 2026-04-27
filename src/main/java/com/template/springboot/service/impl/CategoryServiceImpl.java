package com.template.springboot.service.impl;

import com.template.springboot.dto.CategoryDto;
import com.template.springboot.exception.ResourceNotFoundException;
import com.template.springboot.mapper.CategoryMapper;
import com.template.springboot.model.Category;
import com.template.springboot.repository.CategoryRepository;
import com.template.springboot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAllByOrderByNameAsc(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDto findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categoryMapper.updateFromDto(dto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categoryRepository.delete(category);
    }
}
