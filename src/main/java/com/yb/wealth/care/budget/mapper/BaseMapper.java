package com.yb.wealth.care.budget.mapper;

import com.yb.wealth.care.budget.constant.Currency;
import org.apache.commons.lang3.EnumUtils;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper {

    @Named("mapCurrencyField")
    default Currency mapCurrencyField(final String currency) {
        if (currency == null || currency.isEmpty() || !EnumUtils.isValidEnum(Currency.class, currency)) {
            return null;
        }
        return Currency.valueOf(currency);
    }

    @org.mapstruct.Named("stringToList")
    default List<String> stringToList(String tags) {
        String[] arr = tags == null || tags.isEmpty() ? new String[0] :  tags.split(",");
        return Arrays.stream(arr).toList().stream().map(String::trim).collect(Collectors.toList());
    }

    @org.mapstruct.Named("listToString")
    default String listToString(List<String> tags) {
        return tags == null || tags.isEmpty() ? "" : String.join(",", tags);
    }

}
