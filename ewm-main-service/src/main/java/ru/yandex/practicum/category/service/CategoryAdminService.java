package ru.yandex.practicum.category.service;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);
    void removeCategory(Long categoryId);
    CategoryDto changeCategory(Long categoryId, NewCategoryDto newCategoryDto);
}
