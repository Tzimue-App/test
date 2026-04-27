package com.template.springboot.service;

import com.template.springboot.dto.CategoryDto;
import com.template.springboot.exception.ResourceNotFoundException;
import com.template.springboot.mapper.CategoryMapper;
import com.template.springboot.model.Category;
import com.template.springboot.repository.CategoryRepository;
import com.template.springboot.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService unit tests")
class CategoryServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock CategoryMapper     categoryMapper;

    @InjectMocks CategoryServiceImpl categoryService;

    private Category  category;
    private CategoryDto dto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        dto = new CategoryDto();
        dto.setId(1L);
        dto.setName("Electronics");
    }

    @Test
    @DisplayName("findAll returns paginated DTOs")
    void findAll_returnsMappedPage() {
        var pageable = PageRequest.of(0, 10);
        given(categoryRepository.findAllByOrderByNameAsc(pageable))
                .willReturn(new PageImpl<>(List.of(category)));
        given(categoryMapper.toDto(category)).willReturn(dto);

        Page<CategoryDto> result = categoryService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when not found")
    void findById_notFound_throwsException() {
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create persists and returns DTO")
    void create_savesAndReturnsDto() {
        given(categoryMapper.toEntity(dto)).willReturn(category);
        given(categoryRepository.save(category)).willReturn(category);
        given(categoryMapper.toDto(category)).willReturn(dto);

        CategoryDto result = categoryService.create(dto);

        assertThat(result.getName()).isEqualTo("Electronics");
        then(categoryRepository).should(times(1)).save(category);
    }

    @Test
    @DisplayName("delete removes existing category")
    void delete_existingCategory_callsDelete() {
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        willDoNothing().given(categoryRepository).delete(category);

        assertThatCode(() -> categoryService.delete(1L)).doesNotThrowAnyException();
        then(categoryRepository).should().delete(category);
    }
}
