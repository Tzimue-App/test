package com.template.springboot.mapper;

import com.template.springboot.dto.ItemDto;
import com.template.springboot.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "category.id",   target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ItemDto toDto(Item item);

    @Mapping(target = "category",   ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    Item toEntity(ItemDto dto);

    @Mapping(target = "category",   ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    void updateFromDto(ItemDto dto, @MappingTarget Item item);
}
