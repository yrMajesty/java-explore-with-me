package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category fromDto(CategoryDto dto);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtos(List<Category> categories);
}