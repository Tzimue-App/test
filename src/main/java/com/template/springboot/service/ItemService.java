package com.template.springboot.service;

import com.template.springboot.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    Page<ItemDto> findAll(Pageable pageable);

    Page<ItemDto> findByCategory(Long categoryId, Pageable pageable);

    ItemDto findById(Long id);

    ItemDto create(ItemDto dto);

    ItemDto update(Long id, ItemDto dto);

    void delete(Long id);
}
