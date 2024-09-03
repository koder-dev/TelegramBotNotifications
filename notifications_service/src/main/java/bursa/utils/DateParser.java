package bursa.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeParser {

    private static final Map<String, DayOfWeek> UA_WEEKDAYS = Map.ofEntries(
            Map.entry("Понеділок", DayOfWeek.MONDAY),
            Map.entry("Вівторок", DayOfWeek.TUESDAY),
            Map.entry("Середа", DayOfWeek.WEDNESDAY),
            Map.entry("Четвер", DayOfWeek.THURSDAY),
            Map.entry("Пятниця", DayOfWeek.FRIDAY),
            Map.entry("Субота", DayOfWeek.SATURDAY),
            Map.entry("Неділя", DayOfWeek.SUNDAY)
    );

    public static LocalDateTime parse(String input) throws IllegalArgumentException {
        input = input.trim().toLowerCase();

        if (input.matches("\\d{1,2}$")) {
            return parseTodayTime(input);
        } else if (input.equals("завтра")) {
            return parseTomorrow(input);
        } else if (input.matches("^завтра в \\d{1,2}[:\\.]\\d{2}$")) {
            return parseTomorrowTime(input);
        } else if (input.matches("^в [а-я]+ в \\d{1,2}[:\\.]\\d{2}$")) {
            return parseWeekdayTime(input);
        } else if (input.matches("^через \\d+ минут$") || input.matches("^через \\d+ мин$")) {
            return parseInMinutes(input);
        } else if (input.matches("^через час$")) {
            return parseInHours(input);
        } else if (input.matches("^\\d{2}\\.\\d{2}\\.\\d{4} в \\d{1,2}$")) {
            return parseSpecificDateTime(input);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат даты и времени.");
        }
    }

    private static LocalDateTime parseTodayTime(String input) {
        String[] parts = input.split(" ");
        int hour = Integer.parseInt(parts[1]);
        LocalDateTime dateTime = LocalDate.now().atTime(hour, 0);
        if (dateTime.isBefore(LocalDateTime.now())) {
            dateTime = dateTime.plusDays(1);
        }
        return dateTime;
    }

    private static LocalDateTime parseTomorrow(String input) {
        LocalDateTime dateTime = LocalDate.now().plusDays(1).atTime(9, 0); // По умолчанию 9:00 утра
        return dateTime;
    }

    private static LocalDateTime parseTomorrowTime(String input) {
        Pattern pattern = Pattern.compile("завтра в (\\d{1,2})[:\\.](\\d{2})");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = Integer.parseInt(matcher.group(2));
            LocalDateTime dateTime = LocalDate.now().plusDays(1).atTime(hour, minute);
            return dateTime;
        }
        throw new IllegalArgumentException("Неправильный формат для 'завтра в HH:mm'");
    }

    private static LocalDateTime parseWeekdayTime(String input) {
        Pattern pattern = Pattern.compile("в ([а-я]+) в (\\d{1,2})[:\\.](\\d{2})");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String weekdayStr = matcher.group(1);
            int hour = Integer.parseInt(matcher.group(2));
            int minute = Integer.parseInt(matcher.group(3));

            DayOfWeek targetDay = UA_WEEKDAYS.get(weekdayStr);
            if (targetDay == null) {
                throw new IllegalArgumentException("Неправильный день недели: " + weekdayStr);
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateTime = now.with(TemporalAdjusters.nextOrSame(targetDay)).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            if (dateTime.isBefore(now)) {
                dateTime = dateTime.plusWeeks(1);
            }
            return dateTime;
        }
        throw new IllegalArgumentException("Неправильный формат для 'в [день недели] в HH:mm'");
    }

    private static LocalDateTime parseInMinutes(String input) {
        Pattern pattern = Pattern.compile("через (\\d+) мин(ут)?");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            int minutes = Integer.parseInt(matcher.group(1));
            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(minutes);
            return dateTime;
        }
        throw new IllegalArgumentException("Неправильный формат для 'через N минут'");
    }

    private static LocalDateTime parseInHours(String input) {
        LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
        return dateTime;
    }

    private static LocalDateTime parseSpecificDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'в' HH");
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
        return dateTime;
    }


    // Тестирование
    public static void main(String[] args) {
        List<String> testInputs = List.of(
                "в 19",
                "завтра",
                "завтра в 20:15",
                "в среду в 15:00",
                "через час",
                "через 20 минут",
                "30.01.2018 в 11",
                "каждые 10 минут"
        );

        for (String input : testInputs) {
            try {
                LocalDateTime result = parse(input);
                System.out.println("Input: '" + input + "' => " + result);
            } catch (Exception e) {
                System.out.println("Input: '" + input + "' => Error: " + e.getMessage());
            }
        }
    }
}
