package kg.founders.core.model.rental;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Сводный отчёт по бронированиям и оплатам.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    // --- Сводка по бронированиям ---
    private int totalBookings;
    private int confirmedBookings;
    private int pendingBookings;
    private int cancelledBookings;

    /** Общая выручка (сумма totalAmount подтверждённых бронирований) */
    private BigDecimal totalRevenue;
    /** Средний чек */
    private BigDecimal averageBookingAmount;
    /** Средняя длительность аренды (дни) */
    private double averageRentalDays;

    // --- Сводка по оплатам ---
    private int totalPayments;
    private int successPayments;
    private int failedPayments;
    private int initiatedPayments;
    private BigDecimal totalPaidAmount;

    // --- Разбивки ---
    /** Бронирования по статусу: { "CONFIRMED": 5, "CANCELLED": 2, ... } */
    private Map<String, Integer> bookingsByStatus;
    /** Бронирования по автомобилю: { "Mercedes-Benz S-Class": 3, ... } */
    private Map<String, Integer> bookingsByVehicle;
    /** Бронирования по классу авто: { "Luxury Sedan": 5, ... } */
    private Map<String, Integer> bookingsByCarClass;
    /** Выручка по автомобилю */
    private Map<String, BigDecimal> revenueByVehicle;
    /** Выручка по месяцам: { "2026-03": 5000.00, ... } */
    private Map<String, BigDecimal> revenueByMonth;

    /** Детальный список бронирований (опционально, для экспорта) */
    private List<BookingReportItem> bookings;
    /** Детальный список оплат */
    private List<PaymentReportItem> payments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingReportItem {
        private Long id;
        private String vehicleName;
        private String carClass;
        private String customerName;
        private String customerEmail;
        private String pickupDate;
        private String dropoffDate;
        private int days;
        private String status;
        private String paymentStatus;
        private BigDecimal totalAmount;
        private BigDecimal prepaymentAmount;
        private String createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentReportItem {
        private Long id;
        private Long bookingId;
        private String vehicleName;
        private String method;
        private String status;
        private BigDecimal amount;
        private String transactionId;
        private String createdAt;
    }
}

