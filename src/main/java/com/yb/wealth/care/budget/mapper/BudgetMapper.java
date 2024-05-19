package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.constant.Currency;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import org.apache.commons.lang3.EnumUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BudgetMapper {

    @Mapping(target = "tags", expression = "java(null != expenseBudget.getTags() ? java.util.Arrays.stream(expenseBudget.getTags().split(\",\")).map(String::trim).collect(java.util.stream.Collectors.toList()): null)" )
    @Mapping(target = "budgetExpenseItems", source = "expenseBudgetDetails")
    BudgetDto toBudgetDto(final ExpenseBudget expenseBudget);

    List<BudgetDto> toBudgets(final List<ExpenseBudget> expenseBudgets);

    @Mapping(target = "tags", expression = "java(null != budgetDto.getTags() ? String.join(\",\", budgetDto.getTags()): null)" )
    @Mapping(target = "currency", source = "currency", qualifiedByName = "mapCurrencyField")
    ExpenseBudget toExpenseBudget(final BudgetBaseDto budgetDto);

    @Named("mapCurrencyField")
    default Currency mapCurrencyField(final String currency) {
        if (currency == null || currency.isEmpty() || !EnumUtils.isValidEnum(Currency.class, currency)) {
            return null;
        }
        return Currency.valueOf(currency);
    }

}
