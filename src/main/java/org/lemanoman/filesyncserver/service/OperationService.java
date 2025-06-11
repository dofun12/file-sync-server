package org.lemanoman.filesyncserver.service;

import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileMoverCallback;
import org.lemanoman.filesyncserver.model.OperationModel;
import org.lemanoman.filesyncserver.model.OperationTypeModel;
import org.lemanoman.filesyncserver.model.StepModel;
import org.lemanoman.filesyncserver.repository.OperationRepository;
import org.lemanoman.filesyncserver.repository.OperationTypeRepository;
import org.lemanoman.filesyncserver.repository.StepRepository;
import org.lemanoman.filesyncserver.tasks.FileComparatorTask;
import org.lemanoman.filesyncserver.tasks.FileMoverTask;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OperationService {
    public static final String OPERATION_COPY = "COPY";
    public static final String OPERATION_SYNC = "SYNC";
    public static final String OPERATION_DELETE = "DELETE";
    public static final String OPERATION_OVERWRITE = "OVERWRITE";
    public ExecutorService executor = Executors.newFixedThreadPool(4);

    final OperationRepository operationRepository;
    final OperationTypeRepository operationTypeRepository;
    final StepRepository stepRepository;

    private void initOperationTypes(){
        if(!operationTypeRepository.findAll().isEmpty()){
            return;
        }
        operationTypeRepository.save(new OperationTypeModel(1L, OPERATION_COPY));
        operationTypeRepository.save(new OperationTypeModel(2L, OPERATION_SYNC));
        operationTypeRepository.save(new OperationTypeModel(3L, OPERATION_DELETE));
        operationTypeRepository.save(new OperationTypeModel(4L, OPERATION_OVERWRITE));
    }

    public List<StepModel> getListSteps(Long operationId) {
        return stepRepository.findAllByOperation_Id(operationId);
    }

    public void runSteps(Long operationId) {
        var operationOpt = operationRepository.findById(operationId);
        if (!operationOpt.isPresent()) {
            return;
        }
        var steps = stepRepository.findAllByOperation_Id(operationId);
        for(var step : steps){
            if ("MKDIR".equals(step.getOperationType())) {
                new FileMoverTask(step.getId(), new FileOperationDto(step.getSourcePath(), step.getTargetPath(), step.getOperationType()), new FileMoverCallback() {
                    @Override
                    public void onStart(String id, FileOperationDto fileOperation) {
                        stepRepository.findById(id).ifPresent(step -> {
                            step.setStatusMessage("Running");
                            stepRepository.save(step);
                        });
                    }

                    @Override
                    public void onFinish(String id, FileOperationDto fileOperation, String status) {
                        stepRepository.findById(id).ifPresent(step -> {
                            step.setStatusMessage(status);
                            step.setFinished(1);
                            stepRepository.save(step);
                        });
                    }
                }).run();
            }
        }
        for(var step : steps){
            var fileOperation = new FileOperationDto(step.getSourcePath(), step.getTargetPath(), step.getOperationType());
            var task =  new FileMoverTask(step.getId(), fileOperation, new FileMoverCallback() {
                @Override
                public void onStart(String id, FileOperationDto fileOperation) {
                    stepRepository.findById(id).ifPresent(step -> {
                        step.setStatusMessage("Running");
                        stepRepository.save(step);
                    });
                }

                @Override
                public void onFinish(String id, FileOperationDto fileOperation, String status) {
                    stepRepository.findById(id).ifPresent(step -> {
                        step.setStatusMessage(status);
                        step.setFinished(1);
                        stepRepository.save(step);
                    });
                }
            });
            executor.submit(task);
        }

    }

    public OperationService(OperationRepository operationRepository, OperationTypeRepository operationTypeRepository, StepRepository stepRepository) {
        this.operationRepository = operationRepository;
        this.operationTypeRepository = operationTypeRepository;
        this.stepRepository = stepRepository;
        initOperationTypes();

    }

    public void deleteStep(String id) {
        stepRepository.deleteById(id);
    }

    public OperationModel getOperationId(Long id) {
        return operationRepository.findById(id).orElse(null);
    }

    public void addStep(String sourcePathKey, String targetPathKey, Long operationTypeId) {
        OperationModel operation = new OperationModel();
        operation.setSourcePathKey(sourcePathKey);
        operation.setTargetPathKey(targetPathKey);
        operation.setOperationType(operationTypeRepository.findById(operationTypeId).orElse(null));
        operationRepository.save(operation);
    }

    public void runOperation(Long operationId) {
        if(operationId == null){
            return;
        }
        final var stepModelList = stepRepository.findAllByOperation_Id(operationId);
        for(var step: stepModelList){
            final FileOperationDto fileOperation = new FileOperationDto(step.getSourcePath(), step.getTargetPath(), step.getOperationType());
            executor.submit(new FileMoverTask(step.getId(), fileOperation, new FileMoverCallback() {
                @Override
                public void onStart(String id, FileOperationDto fileOperation) {
                    stepRepository.findById(step.getId()).ifPresent(step -> {
                        step.setStatusMessage("Iniciado");
                        stepRepository.save(step);
                    });
                }

                @Override
                public void onFinish(String id, FileOperationDto fileOperation, String status) {

                }
            }));
        }

    }

    public void startOperation(Long operationId){
        final var operation = operationRepository.findById(operationId).orElse(null);
        if (operation == null) {
            return;
        }
        String sourcePath = new String(Base64.getDecoder().decode(operation.getSourcePathKey()), StandardCharsets.UTF_8);
        String targetPath = new String(Base64.getDecoder().decode(operation.getTargetPathKey()), StandardCharsets.UTF_8);
        executor.execute(new FileComparatorTask(sourcePath, targetPath, fileOperations -> {
            if (fileOperations == null) {
                return;
            }
            for (var fileOperation : fileOperations) {
                StepModel step = new StepModel();
                step.setId(UUID.randomUUID().toString());
                step.setOperation(operation);
                step.setSourcePath(fileOperation.sourcePath());
                step.setTargetPath(fileOperation.targetPath());
                step.setOperationType(fileOperation.operation());
                step.setFinished(0);
                stepRepository.save(step);
            }
        }));

    }

    public void addOperation(String sourcePathKey, String targetPathKey, Long operationTypeId) {
        OperationModel operation = new OperationModel();
        operation.setSourcePathKey(sourcePathKey);
        operation.setTargetPathKey(targetPathKey);
        operation.setOperationType(operationTypeRepository.findById(operationTypeId).orElse(null));
        operationRepository.save(operation);
    }

    public List<OperationTypeModel> getAllOperationTypes() {
        return operationTypeRepository.findAll();
    }

    public List<OperationModel> getAllOperations() {
        return operationRepository.findAll();
    }
}
