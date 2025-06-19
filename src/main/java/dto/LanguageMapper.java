package dto;

import entity.CountryLanguage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LanguageMapper {
    @Mapping(source = "language", target = "language")
    @Mapping(source = "official", target = "official")
    @Mapping(source = "percentage", target = "percentage")
    Language toLanguage(CountryLanguage countryLanguage);
}