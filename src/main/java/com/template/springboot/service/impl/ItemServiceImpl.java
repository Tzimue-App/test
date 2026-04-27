package com.template.springboot.service.impl;

import com.template.springboot.dto.ItemDto;
import com.template.springboot.exception.ResourceNotFoundException;
import com.template.springboot.mapper.ItemMapper;
import com.template.springboot.model.Category;
import com.template.springboot.model.Item;
import com.template.springboot.repository.CategoryRepository;
import com.template.springboot.repository.ItemRepository;
import com.template.springboot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemMapper itemMapper;

    @Override
    public Page<ItemDto> findAll(Pageable pageable) {
        return itemRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(itemMapper::toDto);
    }

    @Override
    public Page<ItemDto> findByCategory(Long categoryId, Pageable pageable) {
        return itemRepository.findByCategoryId(categoryId, pageable)
                .map(itemMapper::toDto);
    }

    @Override
    public ItemDto findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
        Item item = itemMapper.toEntity(dto);
        item.setCategory(category);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
        itemMapper.updateFromDto(dto, item);
        item.setCategory(category);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        itemRepository.delete(item);
    }
}
