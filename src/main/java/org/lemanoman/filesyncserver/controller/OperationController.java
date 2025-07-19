package org.lemanoman.filesyncserver.controller;

import org.lemanoman.filesyncserver.FileUtils;
import org.lemanoman.filesyncserver.dto.LocationDto;
import org.lemanoman.filesyncserver.dto.OperationDto;
import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.model.OperationModel;
import org.lemanoman.filesyncserver.model.OperationTypeModel;
import org.lemanoman.filesyncserver.repository.OperationRepository;
import org.lemanoman.filesyncserver.service.LocationService;
import org.lemanoman.filesyncserver.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OperationController {

    private final OperationService operationService;
    private final LocationService locationService;


    @Autowired
    public OperationController(OperationService operationService, LocationService locationService) {
        this.operationService = operationService;
        this.locationService = locationService;
    }


    @GetMapping("/operations")
    public String list(Model model) {

        List<LocationModel> locations = locationService.getAllLocations();
        List<LocationDto> locationDtos = locations.stream().map(locationModel -> {
            Long id = locationModel.getId();
            String locationName = locationModel.getName();
            String locationPathKey = locationModel.getPathkey();
            String locationFullPath = FileUtils.fromBase64(locationPathKey);
            return new LocationDto(id, locationName, locationPathKey, locationFullPath);
        }).toList();
        model.addAttribute("locations", locationDtos);

        List<OperationTypeModel> operationTypes = operationService.getAllOperationTypes();
        model.addAttribute("operationTypes", operationTypes);

        List<OperationDto> list = operationService.getAllOperations().stream().map(OperationDto::new).toList();
        model.addAttribute("operations", list);
        return "operations";
    }

    @PostMapping("/operations/save")
    public String saveOperation(String sourcePathKey, String targetPathKey, Long operationTypeId) {
        operationService.addOperation(sourcePathKey, targetPathKey, operationTypeId);
        return "redirect:/operations";
    }

    @PostMapping("/operations/start")
    public String startOperation(String id, @RequestParam(required = false) boolean fast) {
        operationService.startOperation(Long.parseLong(id), fast);
        return "redirect:/operations";
    }

}