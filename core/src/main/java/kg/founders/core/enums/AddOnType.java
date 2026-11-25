package kg.founders.core.enums;

import java.math.BigDecimal;

public enum AddOnType {
    ROOF_TENT(new BigDecimal("15.00")),
    GROUND_TENT(new BigDecimal("10.00")),
    SLEEPING_BAGS(new BigDecimal("5.00")),
    KITCHEN_UTENSILS(new BigDecimal("7.00")),
    REFRIGERATOR(new BigDecimal("8.00")),
    TABLE_AND_CHAIRS(new BigDecimal("6.00")),
    BORDER_DOCUMENTS_KZ(new BigDecimal("20.00")),
    BORDER_DOCUMENTS_UZ(new BigDecimal("20.00")),
    DELIVERY_OFFICE(new BigDecimal("0.00")),
    DELIVERY_CITY(new BigDecimal("15.00")),
    DELIVERY_AIRPORT(new BigDecimal("25.00")),
    // Добавлено при миграции из старого сервиса (коды из справочника service_options)
    ROOF_BOX(new BigDecimal("5.00")),
    DELIVERY_ALMATY(new BigDecimal("170.00")),
    DELIVERY_KARAKOL(new BigDecimal("180.00")),
    DELIVERY_OSH(new BigDecimal("220.00")),
    BORDER_PAPER(new BigDecimal("35.00")),
    BORDER_CP(new BigDecimal("60.00")),
    DOCUMENTS_T(new BigDecimal("20.00")),
    DAYS_OUT_KR(new BigDecimal("10.00"));

    private final BigDecimal pricePerDay;

    AddOnType(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }
}
