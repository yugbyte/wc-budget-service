package com.yb.wealth.care.budget.constant;

public enum Currency {
    INR,
    USD,
    GBP,
    BTC;

    public static Currency getFromString(final String currency) {
        switch (currency) {
            case "INR": return INR;
            case "USD": return USD;
            case "GBP": return GBP;
            case "BTC": return BTC;
            default: return null;
        }
    }
}
