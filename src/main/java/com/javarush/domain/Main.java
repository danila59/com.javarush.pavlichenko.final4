package com.javarush.domain;

import dataSpeedTest.DataAcquisitionSpeedTest;
import entity.City;
import dto.CityCountry;
import service.DataMigrationService;
import java.util.List;


public class Main {

     private final DataMigrationService dataMigrationService = new DataMigrationService();
     private final DataAcquisitionSpeedTest dataAcquisitionSpeedTest = new DataAcquisitionSpeedTest();


    public static void main(String[] args) {

        Main main = new Main();
        List<City> allCities = main.dataMigrationService.fetchData();
        List<CityCountry> preparedData = main.dataMigrationService.transformData(allCities);
        main.dataMigrationService.pushToRedis(preparedData);

        main.dataMigrationService.sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        main.dataAcquisitionSpeedTest.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.dataAcquisitionSpeedTest.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        main.dataMigrationService.shutdown();
    }
}
