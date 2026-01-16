package usm.edolomanji.budgetplanner.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import usm.edolomanji.budgetplanner.api.BudgetsApi;
import usm.edolomanji.budgetplanner.entity.Budget;
import usm.edolomanji.budgetplanner.mapper.BudgetMapper;
import usm.edolomanji.budgetplanner.model.BudgetCreateDto;
import usm.edolomanji.budgetplanner.model.BudgetDto;
import usm.edolomanji.budgetplanner.repository.BudgetRepository;

@RestController
public class BudgetController implements BudgetsApi {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    public BudgetController(BudgetRepository budgetRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
    }

    @Override
    public ResponseEntity<BudgetDto> createBudget(BudgetCreateDto budgetInput) {
        Budget entity = budgetMapper.toEntity(budgetInput);
        Budget saved = budgetRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<BudgetDto> getBudget(UUID budgetId) {
        return budgetRepository.findById(budgetId)
                               .map(budgetMapper::toDto)
                               .map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<BudgetDto>> listBudgets() {
        return ResponseEntity.ok(budgetRepository.findAll()
                                                 .stream()
                                                 .map(budgetMapper::toDto)
                                                 .collect(Collectors.toList()));
    }
}

