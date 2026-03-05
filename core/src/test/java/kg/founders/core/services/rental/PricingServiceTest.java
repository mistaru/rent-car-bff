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
//                Arrays.asList(AddOnType.GPS, AddOnType.CHILD_SEAT), "EUR");
//
//        // base: 200, GPS: 2*5=10, CHILD_SEAT: 2*7=14, addOns=24
//        // subtotal = 224, serviceFee = 22.40, total = 246.40
//        assertThat(result.getBaseAmount()).isEqualByComparingTo("200.00");
//        assertThat(result.getAddOnsAmount()).isEqualByComparingTo("24.00");
//        assertThat(result.getServiceFee()).isEqualByComparingTo("22.40");
//        assertThat(result.getTotalAmount()).isEqualByComparingTo("246.40");
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
