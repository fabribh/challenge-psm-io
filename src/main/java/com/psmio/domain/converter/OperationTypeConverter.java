package com.psmio.domain.converter;

import com.psmio.domain.model.OperationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class OperationTypeConverter implements AttributeConverter<OperationType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OperationType operation) {
        if (operation == null)
            return null;
        return operation.getId();
    }

    @Override
    public OperationType convertToEntityAttribute(Integer id) {
        if (id == null)
            return null;
        return Stream.of(OperationType.values())
                .filter(op -> op.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
