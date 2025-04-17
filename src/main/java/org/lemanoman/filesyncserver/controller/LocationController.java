package org.lemanoman.filesyncserver.controller;

import org.lemanoman.filesyncserver.dto.RequestPathKeyDto;
import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/locations")
    public String listLocations(Model model) {
        List<LocationModel> locations = locationService.getAllLocations();
        model.addAttribute("locations", locations);
        return "locations";
    }

    @PostMapping("/locations/save")
    public String saveLocation(LocationModel locationModel) {
        locationService.saveLocation(locationModel);
        return "redirect:/locations";
    }

    @PostMapping("/locations/delete")
    public String deleteLocation(LocationModel locationModel) {
        locationService.deleteLocationById(locationModel.getId());
        return "redirect:/locations";
    }

}