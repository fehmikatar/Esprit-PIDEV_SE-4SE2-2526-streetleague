package tn.esprit._4se2.pi.entities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tn.esprit._4se2.pi.entities.enums.SportType;

@Converter
public class SportTypeConverter implements AttributeConverter<SportType, String> {

    @Override
    public String convertToDatabaseColumn(SportType attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public SportType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        String normalized = dbData.trim()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');

        return switch (normalized) {
            case "FOOTBALL", "FOOT", "SOCCER" -> SportType.FOOTBALL;
            case "BASKETBALL", "BASKET", "BISBALL" -> SportType.BASKETBALL;
            case "TENNIS", "TENIS", "PADEL" -> SportType.TENNIS;
            case "VOLLEYBALL", "VOLLEY" -> SportType.VOLLEYBALL;
            case "HANDBALL", "HAND" -> SportType.HANDBALL;
            default -> SportType.OTHER;
        };
    }
}
