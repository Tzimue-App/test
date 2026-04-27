package com.template.springboot.mapper;

import com.template.springboot.dto.CategoryDto;
import com.template.springboot.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "itemCount", expression = "java(category.getItems().size())")
    CategoryDto toDto(Category category);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category toEntity(CategoryDto dto);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateFromDto(CategoryDto dto, @MappingTarget Category category);
}
