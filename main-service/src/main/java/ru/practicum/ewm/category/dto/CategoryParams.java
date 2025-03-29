package ru.practicum.ewm.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryParams {

    private NewCategoryDto newCategoryDto;

    private Long catId;

    private Long from;

    private Long size;
}
