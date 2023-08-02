package ru.practicum.mainservice.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto request) {
        log.info("Request to create a category {}", request);
        return categoryService.createCategory(request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") @Positive Long categoryId) {
        log.info("Request to delete a category with id='{}'", categoryId);
        categoryService.deleteCategoryById(categoryId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable(name = "catId") @Positive Long categoryId,
                                      @RequestBody @Valid CategoryDto request) {
        log.info("Request to update a category with id='{}', new parameters={}", categoryId, request);
        return categoryService.updateCategoryById(categoryId, request);
    }
}