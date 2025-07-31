package com.havelsan.api.utils;

import java.lang.reflect.Field;

public class DtoUtils {
    public static boolean hasNullField(Object dto) {
        if (dto == null) return true;

        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.getName().equals("id")){
                field.setAccessible(true);
                try {
                    Object value = field.get(dto);
                    if (value == null) {
                        return true; // Return true if at least 1 value is null.
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return false; // Return false if all the values are valid.
    }
}
