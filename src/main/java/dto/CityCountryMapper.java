package dto;

import entity.City;
import entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CityCountryMapper {
    @Mapping(source = "city.id", target = "id")
    @Mapping(source = "city.cityName", target = "name")
    @Mapping(source = "city.population", target = "population")
    @Mapping(source = "city.district", target = "district")
    @Mapping(source = "country.code", target = "countryCode")
    @Mapping(source = "country.code2", target = "alternativeCountryCode")
    @Mapping(source = "country.name", target = "countryName")
    @Mapping(source = "country.continent", target = "continent")
    @Mapping(source = "country.region", target = "countryRegion")
    @Mapping(source = "country.area", target = "countrySurfaceArea")
    @Mapping(source = "country.population", target = "countryPopulation")
    @Mapping(source = "country.languages", target = "languages")
    CityCountry toDto(City city, Country country);

}
