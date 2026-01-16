package usm.edolomanji.budgetplanner.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import usm.edolomanji.budgetplanner.api.CategoriesApi;
import usm.edolomanji.budgetplanner.entity.Category;
import usm.edolomanji.budgetplanner.mapper.CategoryMapper;
import usm.edolomanji.budgetplanner.model.CategoryCreateDto;
import usm.edolomanji.budgetplanner.model.CategoryDto;
import usm.edolomanji.budgetplanner.repository.CategoryRepository;

@RestController
public class CategoryController implements CategoriesApi {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ResponseEntity<CategoryDto> createCategory(CategoryCreateDto categoryCreateDto) {
        Category entity = categoryMapper.toEntity(categoryCreateDto);
        Category saved = categoryRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<List<CategoryDto>> listCategories() {
        return ResponseEntity.ok(categoryRepository.findAll()
                                                   .stream()
                                                   .map(categoryMapper::toDto)
                                                   .collect(Collectors.toList()));
    }
}

