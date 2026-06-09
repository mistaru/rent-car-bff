package kg.founders.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Утилитный класс для безопасного парсинга параметров HTTP-запросов.
 * Обрабатывает null, пустые строки и строку "null" без выбрасывания исключений.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestParamParser {

    public static String blankToNull(String s) {
        return (s == null || s.isBlank() || "null".equalsIgnoreCase(s)) ? null : s.trim();
    }

    public static Long parseLong(String s) {
        return parseOrNull(s, Long::valueOf);
    }

    public static Integer parseInt(String s) {
        return parseOrNull(s, Integer::valueOf);
    }

    public static BigDecimal parseDecimal(String s) {
        return parseOrNull(s, BigDecimal::new);
    }

    public static LocalDate parseDate(String s) {
        return parseOrNull(s, LocalDate::parse);
    }

    private static <T> T parseOrNull(String s, ParserFunction<T> parser) {
        String v = blankToNull(s);
        if (v == null) return null;
        try {
            return parser.apply(v);
        } catch (Exception e) {
            return null;
        }
    }

    @FunctionalInterface
    private interface ParserFunction<T> {
        T apply(String value) throws Exception;
    }
}

