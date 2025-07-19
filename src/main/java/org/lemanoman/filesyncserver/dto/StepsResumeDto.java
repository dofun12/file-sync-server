package org.lemanoman.filesyncserver.dto;

import org.lemanoman.filesyncserver.model.StepModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StepsResumeDto {
    private Integer totalSteps;
    private Double totalSize;
    private Map<String, Integer> sumOperations;
    private Map<String, Double> sumSizeOperations;
    private Integer totalFinished;
    private Map<String, Integer> sumStatusMessage;

    public StepsResumeDto() {}

    public StepsResumeDto(List<StepModel> steps){
        this.totalSteps = steps.size();
        this.totalSize = steps.stream().mapToDouble(StepModel::getSize).sum();

        this.sumOperations = steps.stream()
                .collect(Collectors.groupingBy(StepModel::getOperationType, Collectors.summingInt(value -> 1)));

        this.sumSizeOperations = steps.stream()
                .collect(Collectors.groupingBy(StepModel::getOperationType, Collectors.summingDouble(StepModel::getSize)));
        this.totalFinished = (int) steps.stream().filter(stepModel -> stepModel.getFinished()>0).count();
        this.sumStatusMessage = steps.stream().filter(stepModel -> stepModel.getStatusMessage()!=null)
                .collect(Collectors.groupingBy(StepModel::getStatusMessage, Collectors.summingInt(value -> 1)));
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public Integer getTotalFinished() {
        return totalFinished;
    }

    public void setTotalFinished(Integer totalFinished) {
        this.totalFinished = totalFinished;
    }

    public Map<String, Integer> getSumStatusMessage() {
        return sumStatusMessage;
    }

    public void setSumStatusMessage(Map<String, Integer> sumStatusMessage) {
        this.sumStatusMessage = sumStatusMessage;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public Double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Double totalSize) {
        this.totalSize = totalSize;
    }

    public Map<String, Integer> getSumOperations() {
        return sumOperations;
    }

    public void setSumOperations(Map<String, Integer> sumOperations) {
        this.sumOperations = sumOperations;
    }

    public Map<String, Double> getSumSizeOperations() {
        return sumSizeOperations;
    }

    public void setSumSizeOperations(Map<String, Double> sumSizeOperations) {
        this.sumSizeOperations = sumSizeOperations;
    }
}
