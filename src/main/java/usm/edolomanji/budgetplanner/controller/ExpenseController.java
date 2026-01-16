package usm.edolomanji.budgetplanner.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import usm.edolomanji.budgetplanner.api.ExpensesApi;
import usm.edolomanji.budgetplanner.entity.Category;
import usm.edolomanji.budgetplanner.entity.Expense;
import usm.edolomanji.budgetplanner.mapper.ExpenseMapper;
import usm.edolomanji.budgetplanner.model.ExpenseCreateDto;
import usm.edolomanji.budgetplanner.model.ExpenseDto;
import usm.edolomanji.budgetplanner.repository.CategoryRepository;
import usm.edolomanji.budgetplanner.repository.ExpenseRepository;

@RestController
public class ExpenseController implements ExpensesApi {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseController(ExpenseRepository expenseRepository,
                             CategoryRepository categoryRepository,
                             ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public ResponseEntity<ExpenseDto> createExpense(ExpenseCreateDto expenseInput) {
        Category category = categoryRepository.findById(expenseInput.getCategoryId())
                                              .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Expense entity = expenseMapper.toEntity(expenseInput);
        entity.setCategory(category);

        Expense saved = expenseRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseMapper.toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deleteExpense(UUID expenseId) {
        if (expenseRepository.existsById(expenseId)) {
            expenseRepository.deleteById(expenseId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<ExpenseDto>> listExpenses(UUID categoryId, LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDate != null ? startDate.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime endDateTime = endDate != null
                                     ? endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC)
                                     : null;

        List<Expense> expenses = expenseRepository.findExpenses(categoryId, startDateTime, endDateTime);
        return ResponseEntity.ok(expenses.stream().map(expenseMapper::toDto).collect(Collectors.toList()));
    }
}

