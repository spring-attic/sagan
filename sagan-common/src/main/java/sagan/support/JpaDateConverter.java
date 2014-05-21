package sagan.support;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.util.Date;

@Converter
public class JpaDateConverter implements AttributeConverter<LocalDateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDateTime date) {
        return DateConverter.toDate(date);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date dbData) {
        return DateConverter.toLocalDateTime(dbData);
    }
}
