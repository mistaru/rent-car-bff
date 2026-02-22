package kg.founders.core.exceptions;

import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public static Supplier<NotFoundException> fromMessage(String message) {
        return () -> {
            return new NotFoundException(message);
        };
    }
}