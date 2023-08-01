package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.service.mapper.CategoryMapper;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.fromDto(categoryDto);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategoryById(Long categoryId, CategoryDto request) {
        Category category = getCategoryByIdIfExist(categoryId);

        category.setName(request.getName());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategoryById(Long categoryId) {
        Category category = getCategoryByIdIfExist(categoryId);
        categoryRepository.delete(category);
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();

        return categoryMapper.toDtos(categories);
    }

    public CategoryDto getCategoryById(Long categoryId) {
        Category category = getCategoryByIdIfExist(categoryId);

        return categoryMapper.toDto(category);
    }

    public Category getCategoryByIdIfExist(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Category with id='%s' not found", categoryId)));
    }
}
