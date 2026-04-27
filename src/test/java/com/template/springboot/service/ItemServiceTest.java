package com.template.springboot.service;

import com.template.springboot.dto.ItemDto;
import com.template.springboot.exception.ResourceNotFoundException;
import com.template.springboot.mapper.ItemMapper;
import com.template.springboot.model.Category;
import com.template.springboot.model.Item;
import com.template.springboot.repository.CategoryRepository;
import com.template.springboot.repository.ItemRepository;
import com.template.springboot.service.impl.ItemServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService unit tests")
class ItemServiceTest {

    @Mock private ItemRepository itemRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ItemMapper itemMapper;

    @InjectMocks private ItemServiceImpl itemService;

    private Category category;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();
        item = Item.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .category(category)
                .build();

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Laptop");
        itemDto.setPrice(new BigDecimal("999.99"));
        itemDto.setCategoryId(1L);
    }

    @Test
    @DisplayName("findAll returns paginated items")
    void findAll_returnsItems() {
        var pageable = PageRequest.of(0, 10);
        given(itemRepository.findAllByOrderByCreatedAtDesc(pageable)).willReturn(new PageImpl<>(List.of(item)));
        given(itemMapper.toDto(item)).willReturn(itemDto);

        Page<ItemDto> result = itemService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findByCategory returns filtered items")
    void findByCategory_returnsItems() {
        var pageable = PageRequest.of(0, 10);
        given(itemRepository.findByCategoryId(1L, pageable)).willReturn(new PageImpl<>(List.of(item)));
        given(itemMapper.toDto(item)).willReturn(itemDto);

        Page<ItemDto> result = itemService.findByCategory(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("create saves item with category")
    void create_savesItem() {
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(itemMapper.toEntity(itemDto)).willReturn(item);
        given(itemRepository.save(any(Item.class))).willReturn(item);
        given(itemMapper.toDto(item)).willReturn(itemDto);

        ItemDto result = itemService.create(itemDto);

        assertThat(result.getName()).isEqualTo("Laptop");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("create throws exception if category not found")
    void create_categoryNotFound_throwsException() {
        given(categoryRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(itemDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
