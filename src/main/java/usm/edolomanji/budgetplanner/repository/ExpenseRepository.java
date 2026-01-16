package usm.edolomanji.budgetplanner.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usm.edolomanji.budgetplanner.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e WHERE "
           + "(:categoryId IS NULL OR e.category.id = :categoryId) AND "
           + "(:startDate IS NULL OR e.date >= :startDate) AND "
           + "(:endDate IS NULL OR e.date <= :endDate)")
    List<Expense> findExpenses(@Param("categoryId") UUID categoryId,
                               @Param("startDate") OffsetDateTime startDate,
                               @Param("endDate") OffsetDateTime endDate);
}

