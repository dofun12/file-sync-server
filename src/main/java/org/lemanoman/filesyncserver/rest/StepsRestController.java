package org.lemanoman.filesyncserver.rest;

import org.lemanoman.filesyncserver.dto.StepsResumeDto;
import org.lemanoman.filesyncserver.model.StepModel;
import org.lemanoman.filesyncserver.service.OperationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/steps")
public class StepsRestController {
    final private OperationService operationService;
    public StepsRestController(OperationService  operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/list/operation/{operationId}")
    public List<StepModel> getListStepsByOperationId(@PathVariable Long operationId) {
        return operationService.getListSteps(operationId);
    }

    @GetMapping("/status/operation/{operationId}")
    public StepsResumeDto getOperationStatus(@PathVariable Long operationId) {
        var steps =  operationService.getListSteps(operationId);
        return new StepsResumeDto(steps);
    }
}
