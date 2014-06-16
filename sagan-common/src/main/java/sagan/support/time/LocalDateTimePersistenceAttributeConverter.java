package sagan.support.time;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Attribute converter for handling {@link LocalDateTime} in JPA persistence.
 * <p>
 * This is currently not provided out of the box from JPA 2.x, but one should believe that this class is going to be short lived.
 */
@Converter(autoApply = true)
public class LocalDateTimePersistenceAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
