package me.gabrielporto.comandapro.core.domain.order;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Converter(autoApply = false)
public class PaymentMethodArrayConverter implements AttributeConverter<List<PaymentMethod>, String[]> {

    @Override
    public String[] convertToDatabaseColumn(List<PaymentMethod> attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.stream()
                .filter(Objects::nonNull)
                .map(PaymentMethod::name)
                .toArray(String[]::new);
    }

    @Override
    public List<PaymentMethod> convertToEntityAttribute(String[] dbData) {
        if (dbData == null) {
            return List.of();
        }

        return Arrays.stream(dbData)
                .filter(Objects::nonNull)
                .map(PaymentMethod::fromString)
                .collect(Collectors.toList());
    }
}
