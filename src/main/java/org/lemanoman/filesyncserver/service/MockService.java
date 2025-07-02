package org.lemanoman.filesyncserver.service;

import jakarta.annotation.PostConstruct;
import org.lemanoman.filesyncserver.model.LocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Profile("mockservice")
public class MockService {
    final private LocationService locationService;

    public MockService(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostConstruct
    public void init() {
        locationService.addLocationByPath("f:\\musicas");
        locationService.addLocationByPath("E:\\Musicas");

        // Add more mock data as needed
    }
}