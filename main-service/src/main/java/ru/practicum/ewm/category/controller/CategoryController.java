package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryParams;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private static final String ADMIN_API_PREFIX = "/admin/categories";
    private static final String PUBLIC_API_PREFIX = "/categories";

    private final CategoryService categoryService;

    @GetMapping(PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategory(@RequestParam(defaultValue = "0") Long from,
                                         @RequestParam(defaultValue = "10") Long size) {
        log.info("Received GET request for all category with param: from: {}, size: {}", from, size);

        CategoryParams categoryParams = CategoryParams.builder()
                .from(from)
                .size(size)
                .build();

        return categoryService.getCategory(categoryParams);
    }

    @GetMapping(PUBLIC_API_PREFIX + "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Received GET request for category with id: {}", catId);
        return categoryService.getCategoryById(catId);
    }

    @PostMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("Received POST request to create category: {}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping(ADMIN_API_PREFIX + "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Received DELETE request to delete category with id: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(ADMIN_API_PREFIX + "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Received PATCH request to update category with id : {}, update : {}", catId, newCategoryDto);

        CategoryParams categoryParams = CategoryParams.builder()
                .newCategoryDto(newCategoryDto)
                .catId(catId)
                .build();

        return categoryService.updateCategory(categoryParams);
    }
}
