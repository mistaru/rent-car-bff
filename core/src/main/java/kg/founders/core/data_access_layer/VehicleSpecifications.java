package kg.founders.core.data_access_layer;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.BookingStatus;
import kg.founders.core.enums.VehicleStatus;
import kg.founders.core.model.rental.VehicleSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class VehicleSpecifications {

    private VehicleSpecifications() {
    }

    public static Specification<Vehicle> fromSearchRequest(VehicleSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Exclude BOOKED and UNAVAILABLE vehicles
            predicates.add(cb.not(root.get("status").in(VehicleStatus.BOOKED, VehicleStatus.UNAVAILABLE)));

            // Filter by location
            if (request.getLocationId() != null) {
                predicates.add(cb.equal(root.get("location").get("id"), request.getLocationId()));
            }

            // Filter by brand
            if (request.getBrand() != null && !request.getBrand().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("brand")), "%" + request.getBrand().toLowerCase() + "%"));
            }

            // Filter by car class
            if (request.getCarClass() != null && !request.getCarClass().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("carClass")), request.getCarClass().toLowerCase()));
            }

            // Filter by drivetrain
            if (request.getDrivetrain() != null && !request.getDrivetrain().isBlank()
                    && !"all".equalsIgnoreCase(request.getDrivetrain())) {
                predicates.add(cb.equal(cb.lower(root.get("drivetrain")), request.getDrivetrain().toLowerCase()));
            }

            // Filter by fuel type
            if (request.getFuelType() != null && !request.getFuelType().isBlank()
                    && !"all".equalsIgnoreCase(request.getFuelType())) {
                predicates.add(cb.equal(cb.lower(root.get("fuelType")), request.getFuelType().toLowerCase()));
            }

            // Filter by min price
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("pricePerDay"), request.getMinPrice()));
            }

            // Filter by max price
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("pricePerDay"), request.getMaxPrice()));
            }

            // Filter by exact year
            if (request.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), request.getYear()));
            }

            // Filter by year range
            if (request.getYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), request.getYearFrom()));
            }
            if (request.getYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), request.getYearTo()));
            }

            // Exclude vehicles with overlapping active bookings
            if (request.getPickupDate() != null && request.getDropoffDate() != null) {
                Subquery<Long> bookingSubquery = query.subquery(Long.class);
                Root<Booking> bookingRoot = bookingSubquery.from(Booking.class);
                bookingSubquery.select(bookingRoot.get("vehicle").get("id"));

                List<BookingStatus> excludedStatuses = Arrays.asList(BookingStatus.CANCELLED);

                bookingSubquery.where(
                        cb.equal(bookingRoot.get("vehicle").get("id"), root.get("id")),
                        cb.not(bookingRoot.get("status").in(excludedStatuses)),
                        cb.lessThan(bookingRoot.get("pickupDate"), request.getDropoffDate()),
                        cb.greaterThan(bookingRoot.get("dropoffDate"), request.getPickupDate())
                );

                predicates.add(cb.not(cb.exists(bookingSubquery)));
            }

            // Fetch join location to avoid N+1
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("location", JoinType.LEFT);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
