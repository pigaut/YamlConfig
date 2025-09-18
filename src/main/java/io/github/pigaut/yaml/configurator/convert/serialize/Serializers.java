package io.github.pigaut.yaml.configurator.convert.serialize;

import io.github.pigaut.yaml.convert.format.*;

import java.time.*;
import java.util.*;

public class Serializers {

    private Serializers() {}

    public static final Serializer<LocalDate> LOCAL_DATE = FormatUtil::formatLocalDate;

    public static final Serializer<LocalTime> LOCAL_TIME = FormatUtil::formatLocalTime;

    public static final Serializer<LocalDateTime> LOCAL_DATE_TIME = FormatUtil::formatLocalDateTime;

    public static final Serializer<Locale> LOCALE = FormatUtil::formatLocale;

    public static <T> Serializer<T> defaultSerializer() {
        return Object::toString;
    }

}
