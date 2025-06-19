package dataSpeedTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.PropertiesSessionFactoryProvider;
import dao.CityDAO;
import dto.CityCountry;
import entity.City;
import entity.CountryLanguage;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import redis.RedisClientProvider;

import java.util.List;
import java.util.Set;

public class DataAcquisitionSpeedTest {
    private SessionFactory sessionFactory;
    private PropertiesSessionFactoryProvider sessionFactoryProvider;
    private CityDAO cityDAO;
    private RedisClient redisClient;
    private ObjectMapper objectMapper;


    public DataAcquisitionSpeedTest() {
        this.sessionFactoryProvider = new PropertiesSessionFactoryProvider();
        this.sessionFactory = sessionFactoryProvider.getSessionFactory();
        this.cityDAO = new CityDAO(sessionFactory);
        this.redisClient = new RedisClientProvider().prepareRedisClient();
        this.objectMapper = new ObjectMapper();
    }

    public void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    objectMapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDAO.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}
