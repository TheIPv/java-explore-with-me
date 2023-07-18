package ru.yandex.practicum.category.mapper;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.model.Category;

public class CategoryMapper {
    public static Category toCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static Category toCategoryFromDto(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .id(categoryDto.getId())
                .build();
    }

    public static CategoryDto toCategoryDtoFromCategory(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .id(category.getId())
                .build();
    }
}
