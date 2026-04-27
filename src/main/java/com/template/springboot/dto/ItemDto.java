package com.template.springboot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "{validation.name.required}")
    @Size(min = 2, max = 100, message = "{validation.name.size}")
    private String name;

    @Size(max = 500, message = "{validation.description.size}")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.price.positive}")
    private BigDecimal price;

    @NotNull(message = "{validation.category.required}")
    private Long categoryId;

    private String categoryName;
}
