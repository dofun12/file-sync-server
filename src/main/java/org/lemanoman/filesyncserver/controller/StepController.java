package org.lemanoman.filesyncserver.controller;

import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.model.OperationModel;
import org.lemanoman.filesyncserver.model.OperationTypeModel;
import org.lemanoman.filesyncserver.model.StepModel;
import org.lemanoman.filesyncserver.service.LocationService;
import org.lemanoman.filesyncserver.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StepController {

    private final OperationService operationService;


    @Autowired
    public StepController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/steps/operation/{id}")
    public String getOperationSteps(@PathVariable Long id, Model model) {
        OperationModel operation = operationService.getOperationId(id);
        if (operation == null) {
            return "redirect:/operations";
        }
        model.addAttribute("operation", operation);
        List<StepModel> stepModels = operationService.getListSteps(id);
        model.addAttribute("steps", stepModels);
        return "steps";
    }

    @PostMapping("/steps/delete")
    public String deleteStep(String id) {
        operationService.deleteStep(id);
        return "redirect:/steps/operation/" + id;
    }


}