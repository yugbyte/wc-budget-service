package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetMapper {

    @Mapping(target = "tags", expression = "java(null != expenseBudget.getTags() ? java.util.Arrays.stream(expenseBudget.getTags().split(\",\")).map(String::trim).collect(java.util.stream.Collectors.toList()): null)" )
    BudgetDto toBudget(ExpenseBudget expenseBudget);

    List<BudgetDto> toBudgets(List<ExpenseBudget> expenseBudgets);
}
