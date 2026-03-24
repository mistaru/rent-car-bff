package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.entity.rental.Payment;
import kg.founders.core.enums.BookingStatus;
import kg.founders.core.enums.PaymentTransactionStatus;
import kg.founders.core.model.rental.ReportDto;
import kg.founders.core.model.rental.ReportFilterRequest;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.repo.PaymentRepository;
import kg.founders.core.services.rental.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    @Override
    public ReportDto generateReport(ReportFilterRequest filter) {
        log.info("Generating report with filters: dateFrom={}, dateTo={}, bookingStatus={}, vehicleId={}, carClass={}",
                filter.getDateFrom(), filter.getDateTo(), filter.getBookingStatus(),
                filter.getVehicleId(), filter.getCarClass());

        // 1. Загружаем все бронирования и применяем фильтры
        List<Booking> allBookings = bookingRepository.findAllWithDetails();
        List<Booking> filteredBookings = applyBookingFilters(allBookings, filter);

        // 2. Загружаем все оплаты (с JOIN FETCH для booking + vehicle)
        List<Payment> allPayments = paymentRepository.findAllWithBookingDetails();
        Set<Long> filteredBookingIds = filteredBookings.stream()
                .map(Booking::getId).collect(Collectors.toSet());
        List<Payment> filteredPayments = allPayments.stream()
                .filter(p -> filteredBookingIds.contains(p.getBooking().getId()))
                .collect(Collectors.toList());

        // Доп. фильтр по статусу оплаты
        if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().isBlank()) {
            try {
                PaymentTransactionStatus ps = PaymentTransactionStatus.valueOf(filter.getPaymentStatus());
                filteredPayments = filteredPayments.stream()
                        .filter(p -> p.getStatus() == ps)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        // 3. Агрегации — бронирования
        int total = filteredBookings.size();
        int confirmed = (int) filteredBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        int pending = (int) filteredBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING_PAYMENT).count();
        int cancelled = (int) filteredBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        BigDecimal totalRevenue = filteredBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgAmount = total > 0
                ? filteredBookings.stream().map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        double avgDays = total > 0
                ? filteredBookings.stream().mapToInt(Booking::getDays).average().orElse(0)
                : 0;

        // 4. Агрегации — оплаты
        int totalPay = filteredPayments.size();
        int successPay = (int) filteredPayments.stream().filter(p -> p.getStatus() == PaymentTransactionStatus.SUCCESS).count();
        int failedPay = (int) filteredPayments.stream().filter(p -> p.getStatus() == PaymentTransactionStatus.FAILED).count();
        int initPay = (int) filteredPayments.stream().filter(p -> p.getStatus() == PaymentTransactionStatus.INITIATED).count();
        BigDecimal totalPaidAmt = filteredPayments.stream()
                .filter(p -> p.getStatus() == PaymentTransactionStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Разбивки
        Map<String, Integer> byStatus = filteredBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getStatus().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> byVehicle = filteredBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getVehicle().getBrand() + " " + b.getVehicle().getModel(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> byCarClass = filteredBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getVehicle().getCarClass() != null ? b.getVehicle().getCarClass() : "Unknown",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, BigDecimal> revByVehicle = filteredBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        b -> b.getVehicle().getBrand() + " " + b.getVehicle().getModel(),
                        Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)));

        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, BigDecimal> revByMonth = filteredBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        b -> b.getCreatedAt().format(monthFmt),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)));

        // 6. Детальные списки
        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        List<ReportDto.BookingReportItem> bookingItems = filteredBookings.stream()
                .map(b -> ReportDto.BookingReportItem.builder()
                        .id(b.getId())
                        .vehicleName(b.getVehicle().getBrand() + " " + b.getVehicle().getModel())
                        .carClass(b.getVehicle().getCarClass())
                        .customerName(b.getCustomer().getFullName())
                        .customerEmail(b.getCustomer().getEmail())
                        .pickupDate(b.getPickupDate().format(dateFmt))
                        .dropoffDate(b.getDropoffDate().format(dateFmt))
                        .days(b.getDays())
                        .status(b.getStatus().name())
                        .paymentStatus(b.getPaymentStatus().name())
                        .totalAmount(b.getTotalAmount())
                        .prepaymentAmount(b.getPrepaymentAmount())
                        .createdAt(b.getCreatedAt().format(dtFmt))
                        .build())
                .collect(Collectors.toList());

        List<ReportDto.PaymentReportItem> paymentItems = filteredPayments.stream()
                .map(p -> ReportDto.PaymentReportItem.builder()
                        .id(p.getId())
                        .bookingId(p.getBooking().getId())
                        .vehicleName(p.getBooking().getVehicle() != null
                                ? p.getBooking().getVehicle().getBrand() + " " + p.getBooking().getVehicle().getModel()
                                : "N/A")
                        .method(p.getMethod().name())
                        .status(p.getStatus().name())
                        .amount(p.getAmount())
                        .transactionId(p.getTransactionId())
                        .createdAt(p.getCreatedAt().format(dtFmt))
                        .build())
                .collect(Collectors.toList());

        return ReportDto.builder()
                .totalBookings(total)
                .confirmedBookings(confirmed)
                .pendingBookings(pending)
                .cancelledBookings(cancelled)
                .totalRevenue(totalRevenue)
                .averageBookingAmount(avgAmount)
                .averageRentalDays(avgDays)
                .totalPayments(totalPay)
                .successPayments(successPay)
                .failedPayments(failedPay)
                .initiatedPayments(initPay)
                .totalPaidAmount(totalPaidAmt)
                .bookingsByStatus(byStatus)
                .bookingsByVehicle(byVehicle)
                .bookingsByCarClass(byCarClass)
                .revenueByVehicle(revByVehicle)
                .revenueByMonth(revByMonth)
                .bookings(bookingItems)
                .payments(paymentItems)
                .build();
    }

    private List<Booking> applyBookingFilters(List<Booking> bookings, ReportFilterRequest filter) {
        return bookings.stream()
                .filter(b -> {
                    // Фильтр по периоду
                    if (filter.getDateFrom() != null && !filter.getDateFrom().isBlank()) {
                        LocalDate from = LocalDate.parse(filter.getDateFrom());
                        if (b.getCreatedAt().toLocalDate().isBefore(from)) return false;
                    }
                    if (filter.getDateTo() != null && !filter.getDateTo().isBlank()) {
                        LocalDate to = LocalDate.parse(filter.getDateTo());
                        if (b.getCreatedAt().toLocalDate().isAfter(to)) return false;
                    }
                    // Фильтр по статусу бронирования
                    if (filter.getBookingStatus() != null && !filter.getBookingStatus().isBlank()) {
                        try {
                            BookingStatus st = BookingStatus.valueOf(filter.getBookingStatus());
                            if (b.getStatus() != st) return false;
                        } catch (IllegalArgumentException ignored) {}
                    }
                    // Фильтр по автомобилю
                    if (filter.getVehicleId() != null) {
                        if (!b.getVehicle().getId().equals(filter.getVehicleId())) return false;
                    }
                    // Фильтр по классу авто
                    if (filter.getCarClass() != null && !filter.getCarClass().isBlank()) {
                        if (!filter.getCarClass().equals(b.getVehicle().getCarClass())) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}

