package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsBaseItemDto;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import com.yb.wealth.care.budget.resource.dto.ExpenseDetailItemUpsertDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetDetailsMapper extends BaseMapper {
    @Mapping(target = "tags", source = "tags", qualifiedByName = "stringToList")
    BudgetExpenseDetailsItemDto toDto(final ExpenseBudgetDetail expenseBudgetDetail);
    List<BudgetExpenseDetailsItemDto> toBudgetDetailsDto(final List<ExpenseBudgetDetail> expenseBudgetDetail);
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToString")
    ExpenseBudgetDetail toExpenseBudgetDetail(BudgetExpenseDetailsItemDto detailsItemDto);
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToString")
    ExpenseBudgetDetail toExpenseBudgetDetail(BudgetExpenseDetailsBaseItemDto detailsItemDto);
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToString")
    ExpenseBudgetDetail toExpenseBudgetDetail(ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto);
}
