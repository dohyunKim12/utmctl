package util;

import dto.TaskDto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskField {
    private static final List<String> FIELD_LIST = new ArrayList<>();

    static {
        try {
            Class<?> clazz = TaskDto.class;
            FIELD_LIST.add("all");
            for (Field field : clazz.getDeclaredFields()) {
                FIELD_LIST.add(field.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TaskField list", e);
        }
    }

    public static List<String> getAllFields() {
        return FIELD_LIST.stream()
                .filter(field -> !field.equalsIgnoreCase("all"))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static boolean isValidField(String fieldName) {
        return FIELD_LIST.stream()
                .anyMatch(field -> field.equalsIgnoreCase(fieldName));
    }

    public static String get(String fieldName) {
        return FIELD_LIST.stream()
                .filter(field -> field.equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid field name: " + fieldName));
    }
}
