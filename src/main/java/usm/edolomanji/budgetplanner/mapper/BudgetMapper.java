package usm.edolomanji.budgetplanner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import usm.edolomanji.budgetplanner.entity.Budget;
import usm.edolomanji.budgetplanner.model.BudgetCreateDto;
import usm.edolomanji.budgetplanner.model.BudgetDto;

@Mapper
public interface BudgetMapper {

    BudgetDto toDto(Budget entity);

    @Mapping(target = "id",
             ignore = true)
    Budget toEntity(BudgetCreateDto input);
}

