package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.services.rental.BookingEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEmailServiceImpl implements BookingEmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.admin-email}")
    private String adminEmail;

    @Value("${app.mail.admin-whatsapp}")
    private String adminWhatsapp;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(booking.getCustomer().getEmail());
            message.setSubject("Booking Request Received — Iron Horse Asia");
            message.setText(buildCustomerEmailBody(booking));
//            mailSender.send(message); //todo temporary disable email sending until we have actual SMTP credentials
            log.info("Booking confirmation sent to {}", booking.getCustomer().getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email for booking {}: {}",
                    booking.getId(), e.getMessage());
        }
    }

    @Async
    public void sendAdminNotification(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Booking Request #" + booking.getId());
            message.setText(buildAdminEmailBody(booking));
            mailSender.send(message);
            log.info("Admin notification sent for booking #{}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send admin notification for booking {}: {}",
                    booking.getId(), e.getMessage());
        }
    }

    private String buildCustomerEmailBody(Booking booking) {
        String pickupDate  = booking.getPickupDate().format(DATE_FMT);
        String dropoffDate = booking.getDropoffDate().format(DATE_FMT);
//        String addOns      = formatAddOns(booking.getAddOns());
        String addOns      = "SOME ADD-ONS"; // TODO: add actual add-ons when they are implemented
        int    days        = booking.getDays() != null ? booking.getDays() :
                (int)(booking.getDropoffDate().toEpochDay() - booking.getPickupDate().toEpochDay());

        return String.format("""
            Dear %s,

            We confirm that we have received your booking request for a %s %s for %d day(s),
            from %s to %s, under the name: %s,
            with the following additional options: %s.

            Once the status of your booking request changes, you will receive an email notification.
            You can also check your booking status via your personal link.

            You may also clarify any details with our managers at any time:
            Email:     %s
            WhatsApp:  %s

            Best regards,
            Iron Horse Asia Team
            """,
                booking.getCustomer().getFullName(),
                booking.getVehicle().getBrand(),
                booking.getVehicle().getModel(),
                days,
                pickupDate,
                dropoffDate,
                booking.getCustomer().getFullName(),
                addOns,
                adminEmail,
                adminWhatsapp
        );
    }

    private String buildAdminEmailBody(Booking booking) {
        String pickupDate  = booking.getPickupDate().format(DATE_FMT);
        String dropoffDate = booking.getDropoffDate().format(DATE_FMT);
//        String addOns      = formatAddOns(booking.getAddOns());
        String addOns      = "SOME ADD-ONS"; // TODO: add actual add-ons when they are implemented

        return String.format("""
            New booking request received.

            Booking ID:   #%d
            Customer:     %s
            Email:        %s
            Phone:        %s
            Vehicle:      %s %s
            Period:       %s → %s
            Add-ons:      %s
            Total:        $%s

            Please review and confirm in the admin panel.
            """,
                booking.getId(),
                booking.getCustomer().getFullName(),
                booking.getCustomer().getEmail(),
                booking.getCustomer().getPhone(),
                booking.getVehicle().getBrand(),
                booking.getVehicle().getModel(),
                pickupDate,
                dropoffDate,
                addOns,
                booking.getTotalAmount()
        );
    }

    private String formatAddOns(List<String> addOns) {
        if (addOns == null || addOns.isEmpty()) return "None";
        return String.join(", ", addOns);
    }
}
