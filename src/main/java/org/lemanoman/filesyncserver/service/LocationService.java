package org.lemanoman.filesyncserver.service;

import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // Create or Update a Location
    public LocationModel saveLocation(LocationModel location) {
        return locationRepository.save(location);
    }

    // Retrieve all Locations
    public List<LocationModel> getAllLocations() {
        return locationRepository.findAll();
    }

    // Retrieve a Location by ID
    public Optional<LocationModel> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    // Delete a Location by ID
    public void deleteLocationById(Long id) {
        locationRepository.deleteById(id);
    }
}