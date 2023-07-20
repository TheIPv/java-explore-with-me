package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        return CategoryMapper.toCategoryDtoFromCategory(
                categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("This category wasn't found")));
    }
}
