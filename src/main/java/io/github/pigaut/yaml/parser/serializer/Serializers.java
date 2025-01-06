package io.github.pigaut.yaml.parser.serializer;

import io.github.pigaut.yaml.configurator.parser.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class Serializers {

    private Serializers() {}

    public static <T> ConfigSerializer<T> defaultSerializer() {
        return Object::toString;
    }

    public static final ConfigSerializer<LocalDate> LOCAL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")::format;

    public static final ConfigSerializer<LocalTime> LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm:ss")::format;

    public static final ConfigSerializer<LocalDateTime> LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")::format;

    public static final ConfigSerializer<Locale> LOCALE = Locale::toLanguageTag;

}
