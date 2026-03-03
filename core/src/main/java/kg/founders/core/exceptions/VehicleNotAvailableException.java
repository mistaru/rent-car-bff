package kg.founders.core.exceptions;

import org.springframework.http.HttpStatus;

public class VehicleNotAvailableException extends BaseException {
    public VehicleNotAvailableException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
