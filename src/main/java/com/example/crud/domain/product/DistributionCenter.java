package com.example.crud.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum DistributionCenter {
    MOGI_DAS_CRUZES("Mogi das Cruzes", "SP"),
    RECIFE("Recife", "PE"),
    PORTO_ALEGRE("Porto Alegre", "RS");

    private static final Set<String> BRAZILIAN_STATES = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    private final String city;
    private final String uf;

    DistributionCenter(String city, String uf) {
        this.city = city;
        this.uf = uf;
    }

    @JsonValue
    public String getCity() {
        return city;
    }

    public String getUf() {
        return uf;
    }

    @JsonCreator
    public static DistributionCenter from(String value) {
        if (value != null) {
            for (DistributionCenter center : values()) {
                if (normalize(center.city).equals(normalize(value))) {
                    return center;
                }
            }
        }
        throw new IllegalArgumentException("Invalid distribution center '" + value
                + "'. Accepted values: Mogi das Cruzes, Recife, Porto Alegre");
    }

    public static List<DistributionCenter> fromCityOrState(String value) {
        if (value != null) {
            for (DistributionCenter center : values()) {
                if (normalize(center.city).equals(normalize(value))) {
                    return List.of(center);
                }
            }
            String uf = value.trim().toUpperCase();
            if (BRAZILIAN_STATES.contains(uf)) {
                return Arrays.stream(values())
                        .filter(center -> center.uf.equals(uf))
                        .toList();
            }
        }
        throw new IllegalArgumentException("Invalid distribution center or state '" + value
                + "'. Use a city (Mogi das Cruzes, Recife, Porto Alegre) or a state (SP, RJ, MG...)");
    }

    private static String normalize(String text) {
        String noAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccents.replace("_", " ").trim().toLowerCase();
    }
}