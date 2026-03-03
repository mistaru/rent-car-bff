package kg.founders.core.enums;

import java.math.BigDecimal;

public enum AddOnType {
    GPS(new BigDecimal("5.00")),
    CHILD_SEAT(new BigDecimal("7.00")),
    ADDITIONAL_DRIVER(new BigDecimal("10.00")),
    INSURANCE_PREMIUM(new BigDecimal("15.00")),
    WIFI_HOTSPOT(new BigDecimal("3.00"));

    private final BigDecimal pricePerDay;

    AddOnType(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }
}
