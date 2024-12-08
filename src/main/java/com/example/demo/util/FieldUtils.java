package com.example.demo.util;

import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class FieldUtils {

    public static <T> void updateFieldIfNotNull(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }
}
