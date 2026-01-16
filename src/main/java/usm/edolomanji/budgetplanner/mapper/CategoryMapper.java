package usm.edolomanji.budgetplanner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import usm.edolomanji.budgetplanner.entity.Category;
import usm.edolomanji.budgetplanner.model.CategoryCreateDto;
import usm.edolomanji.budgetplanner.model.CategoryDto;

@Mapper
public interface CategoryMapper {

    CategoryDto toDto(Category entity);

    @Mapping(target = "id",
             ignore = true)
    Category toEntity(CategoryCreateDto input);
}

