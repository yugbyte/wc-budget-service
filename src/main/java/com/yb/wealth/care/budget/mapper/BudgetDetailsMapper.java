package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetDetailsMapper {
    @Mapping(source = "expenseCategory.categoryName", target = "expenseCategory")
    @Mapping(source = "expenseCategory.icon", target = "icon")
    BudgetDetailsDto toDto(ExpenseBudgetDetail expenseBudgetDetail);

    List<BudgetDetailsDto> toBudgetDetailsDto(List<ExpenseBudgetDetail> expenseBudgetDetail);
}
