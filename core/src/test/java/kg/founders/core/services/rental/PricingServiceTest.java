package kg.founders.core.services.rental;

import kg.founders.core.enums.AddOnType;
import kg.founders.core.model.rental.PriceBreakdown;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PricingServiceTest {

//    private final PricingService pricingService = new PricingService();
//
//    @Test
//    void shouldCalculateBasicPrice() {
//        PriceBreakdown result = pricingService.calculate(
//                new BigDecimal("100.00"), 3, Collections.emptyList(), "USD");
//
//        assertThat(result.getBaseAmount()).isEqualByComparingTo("300.00");
//        assertThat(result.getAddOnsAmount()).isEqualByComparingTo("0");
//        assertThat(result.getServiceFee()).isEqualByComparingTo("30.00"); // 10% of 300
//        assertThat(result.getTotalAmount()).isEqualByComparingTo("330.00");
//        assertThat(result.getDays()).isEqualTo(3);
//        assertThat(result.getCurrency()).isEqualTo("USD");
//    }
//
//    @Test
//    void shouldCalculatePriceWithAddOns() {
//        PriceBreakdown result = pricingService.calculate(
//                new BigDecimal("100.00"), 2,
//                Arrays.asList(AddOnType.ROOF_TENT, AddOnType.SLEEPING_BAGS), "EUR");
//
//        // base: 200, ROOF_TENT: 2*15=30, SLEEPING_BAGS: 2*5=10, addOns=40
//        // subtotal = 240, serviceFee = 24.00, total = 264.00
//        assertThat(result.getBaseAmount()).isEqualByComparingTo("200.00");
//        assertThat(result.getAddOnsAmount()).isEqualByComparingTo("40.00");
//        assertThat(result.getServiceFee()).isEqualByComparingTo("24.00");
//        assertThat(result.getTotalAmount()).isEqualByComparingTo("264.00");
//        assertThat(result.getAddOnItems()).hasSize(2);
//        assertThat(result.getCurrency()).isEqualTo("EUR");
//    }
//
//    @Test
//    void shouldHandleNullAddOns() {
//        PriceBreakdown result = pricingService.calculate(
//                new BigDecimal("50.00"), 1, null, null);
//
//        assertThat(result.getBaseAmount()).isEqualByComparingTo("50.00");
//        assertThat(result.getAddOnsAmount()).isEqualByComparingTo("0");
//        assertThat(result.getServiceFee()).isEqualByComparingTo("5.00");
//        assertThat(result.getTotalAmount()).isEqualByComparingTo("55.00");
//        assertThat(result.getCurrency()).isEqualTo("USD");
//    }
}
