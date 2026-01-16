package usm.edolomanji.budgetplanner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import usm.edolomanji.budgetplanner.entity.Expense;
import usm.edolomanji.budgetplanner.model.ExpenseCreateDto;
import usm.edolomanji.budgetplanner.model.ExpenseDto;

@Mapper
public interface ExpenseMapper {

    @Mapping(target = "categoryId",
             source = "category.id")
    @Mapping(target = "categoryName",
             source = "category.name")
    ExpenseDto toDto(Expense entity);

    @Mapping(target = "id",
             ignore = true)
    @Mapping(target = "category.id",
             source = "categoryId")
    Expense toEntity(ExpenseCreateDto input);
}

