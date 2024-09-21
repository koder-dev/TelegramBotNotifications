package bursa.utils;

import bursa.service.exceptions.NotCorrectDateFormat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bursa.strings.TelegramTextResponses.NOT_FOUND_DATE_TEXT;

public class DateTimeParser {
    private DateTimeParser() {}

    private static final String TIME_PATTERN = "(\\d{1,2})[:.]?(\\d{2})?";
    private static final String DATE_PATTERN = "(\\d{1,2})[./](\\d{1,2})[./](\\d{4})?";
    private static final String DAY_OF_WEEK_PATTERN = "(понеділок|вівторок|середа|четвер|п'ятниця|субота|неділя)";
    private static final String DAY_MONTH_PATTERN = "(\\d{1,2})\\s+(січня|лютого|березня|квітня|травня|червня|липня|серпня|вересня|жовтня|листопада|грудня)";
    private static final String RELATIVE_DATE_PATTERN = "(завтра|післязавтра|сьогодні|ранку|вечора)?\\s?([ов])?\\s?(\\d{1,2}[:.]?\\d{2})\\s?(завтра|післязавтра|сьогодні|ранку|вечора)?";


    public static LocalDateTime parse(String input) throws NotCorrectDateFormat, DateTimeException {
        LocalTime time = extractTime(input);
        LocalDate date = extractDate(input);

        if (Objects.isNull(date)) {
            date = extractDayOfWeek(input);
            if (Objects.isNull(date)) {
                date = extractDayMonth(input);
            }
            if (Objects.isNull(date)) {
                date = extractRelativeDate(input);
            }
        }
        if (Objects.isNull(time) && Objects.isNull(date)) {
            throw new NotCorrectDateFormat(NOT_FOUND_DATE_TEXT);
        }
        if (Objects.isNull(time)) {
            time = LocalTime.now();
        }
        if (Objects.isNull(date)) {
            date = LocalDate.now();
        }

        LocalDateTime result = LocalDateTime.parse(LocalDateTime.of(date, time).format(DateTimeFormatter.ISO_DATE_TIME));
        return result.isAfter(LocalDateTime.now()) ? result : result.plusDays(1);
    }

    private static LocalTime extractTime(String input) {
        Pattern timePattern = Pattern.compile(TIME_PATTERN);
        Matcher matcher = timePattern.matcher(input);

        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = Objects.isNull(matcher.group(2)) ? 0 : (Integer.parseInt(matcher.group(2)));
            return LocalTime.of(hour, minute);
        }
        return null;
    }

    private static LocalDate extractDate(String input) {
        Pattern datePattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = datePattern.matcher(input);

        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Objects.nonNull(matcher.group(3)) ? Integer.parseInt(matcher.group(3)) : LocalDate.now().getYear();
            return LocalDate.of(year, month, day);
        }
        return null;
    }

    private static LocalDate extractRelativeDate(String input) {
        Pattern relativeDatePattern = Pattern.compile(RELATIVE_DATE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = relativeDatePattern.matcher(input);

        if (matcher.find()) {
            String relativeDate = Objects.isNull(matcher.group(1)) ? matcher.group(4) : matcher.group(1);
            if (Objects.isNull(relativeDate)) return null;
            LocalDate now = LocalDate.now();
            LocalTime time = extractTime(matcher.group(3));

            return switch (relativeDate) {
                case "завтра" -> now.plusDays(1);
                case "післязавтра" -> now.plusDays(2);
                case "сьогодні" -> now;
                case "ранку", "вечора" -> now.plusMonths(LocalTime.now().isAfter(time) ? 1 : 0);
                default -> null;
            };
        }
        return null;
    }


    private static LocalDate extractDayOfWeek(String input) {
        Pattern dayOfWeekPattern = Pattern.compile(DAY_OF_WEEK_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = dayOfWeekPattern.matcher(input);

        if (matcher.find()) {
            String dayOfWeekString = matcher.group(1).toLowerCase(Locale.ROOT);
            DayOfWeek dayOfWeek = convertStringToDayOfWeek(dayOfWeekString);

            LocalDate now = LocalDate.now();
            return now.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        }
        return null;
    }

    private static DayOfWeek convertStringToDayOfWeek(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "понеділок" -> DayOfWeek.MONDAY;
            case "вівторок" -> DayOfWeek.TUESDAY;
            case "середа" -> DayOfWeek.WEDNESDAY;
            case "четвер" -> DayOfWeek.THURSDAY;
            case "п'ятниця" -> DayOfWeek.FRIDAY;
            case "субота" -> DayOfWeek.SATURDAY;
            case "неділя" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Невірний день тижня: " + dayOfWeek);
        };
    }

    private static LocalDate extractDayMonth(String input) {
        Pattern dayMonthPattern = Pattern.compile(DAY_MONTH_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = dayMonthPattern.matcher(input);

        if (matcher.find()) {
            int day = Integer.parseInt(matcher.group(1));
            String monthString = matcher.group(2).toLowerCase(Locale.ROOT);
            int month = convertStringToMonth(monthString);

            LocalDate now = LocalDate.now();
            int year = now.getYear();

            if (now.isAfter(LocalDate.of(year, month, day))) {
                year++;
            }

            return LocalDate.of(year, month, day);
        }
        return null;
    }

    private static int convertStringToMonth(String month) {
        return switch (month) {
            case "січня" -> 1;
            case "лютого" -> 2;
            case "березня" -> 3;
            case "квітня" -> 4;
            case "травня" -> 5;
            case "червня" -> 6;
            case "липня" -> 7;
            case "серпня" -> 8;
            case "вересня" -> 9;
            case "жовтня" -> 10;
            case "листопада" -> 11;
            case "грудня" -> 12;
            default -> throw new IllegalArgumentException("Невірний місяць: " + month);
        };
    }
}
