package ru.yandex.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> getAll(Pageable pageable);
    
    CategoryDto getById(Long categoryId);
}
