package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.resource.dto.BudgetBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
uses = BudgetDetailsMapper.class)
public interface BudgetMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "stringToList")
    @Mapping(target = "budgetExpenseItems", source = "expenseBudgetDetails")
    BudgetDto toBudgetDto(final ExpenseBudget expenseBudget);

    List<BudgetDto> toBudgets(final List<ExpenseBudget> expenseBudgets, @Context BudgetDetailsMapper budgetDetailsMapper);

    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToString")
    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrencyField")
    ExpenseBudget toExpenseBudget(final BudgetBaseDto budgetDto);

}
