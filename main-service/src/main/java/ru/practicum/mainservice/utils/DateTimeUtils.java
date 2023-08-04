package ru.practicum.mainservice.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.exception.DateTimeValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@UtilityClass
public class DateTimeUtils {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final Integer HOURS_BEFORE_START_EVENT_ADMIN = 1;
    private static final Integer HOURS_BEFORE_START_EVENT_USER = 2;

    public void checkEndIsAfterStart(LocalDateTime startDate, LocalDateTime endDate) {
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate) && startDate.isAfter(endDate)) {
            throw new DateTimeValidationException("The start date cannot be later than the end date");
        }
    }

    public void checksPeriodBeforeStartDate(LocalDateTime startDate, boolean isAdmin) {
        LocalDateTime minStartDate;

        if (isAdmin) {
            minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT_ADMIN);
        } else {
            minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT_USER);
        }

        if (startDate.isBefore(minStartDate)) {
            String message = isAdmin
                    ? String.format("Before the event less than %s hours", HOURS_BEFORE_START_EVENT_ADMIN)
                    : String.format("Before the event less than %s hours", HOURS_BEFORE_START_EVENT_USER);

            throw new DateTimeValidationException(message);
        }
    }
}
