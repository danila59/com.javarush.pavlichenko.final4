package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.PropertiesSessionFactoryProvider;
import dao.CityDAO;
import dao.CountryDAO;
import dto.CityCountry;
import dto.CityCountryMapper;
import dto.Language;
import entity.City;
import entity.Country;
import entity.CountryLanguage;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mapstruct.factory.Mappers;
import redis.RedisClientProvider;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.nonNull;

public class DataMigrationService {
    private final RedisClient redisClient;
    private final ObjectMapper mapper;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    public SessionFactory sessionFactory;
    private final PropertiesSessionFactoryProvider propertiesSessionFactoryProvider;


    public DataMigrationService() {
        RedisClientProvider redisClientProvider = new RedisClientProvider();
        this.redisClient = redisClientProvider.prepareRedisClient();
        this.mapper = new ObjectMapper();
        this.propertiesSessionFactoryProvider = new PropertiesSessionFactoryProvider();
        this.sessionFactory = this.propertiesSessionFactoryProvider.getSessionFactory();
        this.countryDAO = new CountryDAO(sessionFactory);
        this.cityDAO = new CityDAO(sessionFactory);
    }

    public List<CityCountry> transformData(List<City> cities) {
        CityCountryMapper mapper = Mappers.getMapper(CityCountryMapper.class);

        List<CityCountry> cityCountries = new ArrayList<>();
        for (City city : cities) {
            cityCountries.add(mapper.toDto(city, city.getCountry()));
        }
        return cityCountries;
    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            List<Country> countries = countryDAO.getAll();
            int totalCount = cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    public void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
