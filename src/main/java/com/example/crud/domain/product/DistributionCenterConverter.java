package com.example.crud.domain.product;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DistributionCenterConverter implements AttributeConverter<DistributionCenter, String> {

    @Override
    public String convertToDatabaseColumn(DistributionCenter distributionCenter) {
        return distributionCenter == null ? null : distributionCenter.getCity();
    }

    @Override
    public DistributionCenter convertToEntityAttribute(String value) {
        return value == null ? null : DistributionCenter.from(value);
    }
}