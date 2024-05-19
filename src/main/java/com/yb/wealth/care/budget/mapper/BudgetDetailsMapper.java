package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsBaseItemDto;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import com.yb.wealth.care.budget.resource.dto.ExpenseDetailItemUpsertDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetDetailsMapper {
    BudgetExpenseDetailsItemDto toDto(final ExpenseBudgetDetail expenseBudgetDetail);
    List<BudgetExpenseDetailsItemDto> toBudgetDetailsDto(final List<ExpenseBudgetDetail> expenseBudgetDetail);
    ExpenseBudgetDetail toExpenseBudgetDetail(BudgetExpenseDetailsItemDto detailsItemDto);
    ExpenseBudgetDetail toExpenseBudgetDetail(BudgetExpenseDetailsBaseItemDto detailsItemDto);
    ExpenseBudgetDetail toExpenseBudgetDetail(ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto);

}
