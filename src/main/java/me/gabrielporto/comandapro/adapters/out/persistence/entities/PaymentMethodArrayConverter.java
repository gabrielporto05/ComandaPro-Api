package me.gabrielporto.comandapro.adapters.out.persistence.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.gabrielporto.comandapro.core.domain.order.PaymentMethod;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Converter
public class PaymentMethodArrayConverter implements AttributeConverter<PaymentMethod[], String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(PaymentMethod[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        return Arrays.stream(attribute)
                .filter(Objects::nonNull)
                .map(Enum::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public PaymentMethod[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new PaymentMethod[0];
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(PaymentMethod::valueOf)
                .toArray(PaymentMethod[]::new);
    }
}
