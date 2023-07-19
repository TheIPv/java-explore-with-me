package ru.yandex.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.dto.NewCategoryDto;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDtoFromCategory(categoryRepository
                .save(CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto)));
    }

    @Override
    public void removeCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto changeCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDtoFromCategory(
                categoryRepository.save(Category.builder()
                        .id(categoryId)
                        .name(newCategoryDto.getName())
                        .build()
        ));
    }
}
