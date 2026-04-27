package com.template.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank(message = "{validation.name.required}")
    @Size(min = 2, max = 100, message = "{validation.name.size}")
    private String name;

    @Size(max = 500, message = "{validation.description.size}")
    private String description;

    private int itemCount;
}
