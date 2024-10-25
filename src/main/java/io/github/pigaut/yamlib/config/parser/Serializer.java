package io.github.pigaut.yamlib.config.parser;

import org.jetbrains.annotations.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public interface Serializer<T> {

    @NotNull String serialize(@NotNull T value);

    static <T> Serializer<T> defaultSerializer() {
        return Object::toString;
    }

    Serializer<LocalDate> LOCAL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")::format;

    Serializer<LocalTime> LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm:ss")::format;

    Serializer<LocalDateTime> LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")::format;

    Serializer<Locale> LOCALE = Locale::toLanguageTag;

}
