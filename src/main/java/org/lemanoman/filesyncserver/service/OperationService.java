package org.lemanoman.filesyncserver.service;

import org.lemanoman.filesyncserver.dto.FileOperationDto;
import org.lemanoman.filesyncserver.interfaces.FileComparatorCallback;
import org.lemanoman.filesyncserver.interfaces.FileMoverCallback;
import org.lemanoman.filesyncserver.model.OperationModel;
import org.lemanoman.filesyncserver.model.OperationTypeModel;
import org.lemanoman.filesyncserver.model.StepModel;
import org.lemanoman.filesyncserver.repository.OperationRepository;
import org.lemanoman.filesyncserver.repository.OperationTypeRepository;
import org.lemanoman.filesyncserver.repository.StepRepository;
import org.lemanoman.filesyncserver.tasks.FileComparatorTask;
import org.lemanoman.filesyncserver.tasks.FileMoverTask;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OperationService {
    public static final String OPERATION_COPY = "COPY";
    public static final String OPERATION_SYNC = "SYNC";
    public static final String OPERATION_DELETE = "DELETE";
    public static final String OPERATION_OVERWRITE = "OVERWRITE";
    public ExecutorService executor = Executors.newFixedThreadPool(4);
    final Logger logger = org.slf4j.LoggerFactory.getLogger(OperationService.class);
    final OperationRepository operationRepository;
    final OperationTypeRepository operationTypeRepository;
    final StepRepository stepRepository;
    private ConcurrentHashMap<Long,Double> atomicSizeSum = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long,Integer> atomicScannedFiles = new ConcurrentHashMap<>();

    private void initOperationTypes() {
        if (!operationTypeRepository.findAll().isEmpty()) {
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
        if (operationOpt.isEmpty()) {
            return;
        }
        final var ops = operationOpt.get();
        if(Objects.equals(ops.getReady(), 0)) {
            logger.warn("Operation {} is not ready to run steps", operationId);
            return;
        }

        ops.setRunning(1);
        operationRepository.save(ops);
        var steps = stepRepository.findAllByOperation_Id(operationId);
        for (var step : steps) {
            if ("MKDIR".equals(step.getOperationType())) {
                new FileMoverTask(step.getId(), new FileOperationDto(step.getSourcePath(), step.getTargetPath(), step.getSize(), step.getOperationType()), new FileMoverCallback() {
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
        final int stepsSize = steps.size();
        AtomicInteger totalFinished = new AtomicInteger(0);
        for (var step : steps) {
            var fileOperation = new FileOperationDto(step.getSourcePath(), step.getTargetPath(), step.getSize(), step.getOperationType());
            var task = new FileMoverTask(step.getId(), fileOperation, new FileMoverCallback() {
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
                        final int finished = totalFinished.incrementAndGet();
                        if(finished==stepsSize){
                            var operation = operationRepository.findById(operationId).orElse(null);
                            if (operation != null) {
                                operation.setRunning(0);
                                operation.setReady(1);
                                operation.setFinished(1);
                                operationRepository.save(operation);
                            }
                        }
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
        if (operationId == null) {
            return;
        }
        final var stepModelList = stepRepository.findAllByOperation_Id(operationId);
        for (var step : stepModelList) {
            final FileOperationDto fileOperation = new FileOperationDto(step.getSourcePath(), step.getTargetPath(),step.getSize(), step.getOperationType());
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

    public void startOperation(Long operationId, boolean fast) {
        final var operation = operationRepository.findById(operationId).orElse(null);
        if (operation == null) {
            return;
        }
        operation.setStarted(1);
        operation.setReady(0);
        operation.setRunning(0);
        operation.setTotalScannedFiles(0);
        atomicSizeSum.put(operationId, 0.0);
        operationRepository.saveAndFlush(operation);
        var steps = stepRepository.findAllByOperation_Id(operationId);
        if(!steps.isEmpty()) {
            for(var step : steps) {
                stepRepository.deleteById(step.getId());
            }
        }
        stepRepository.flush();

        String sourcePath = new String(Base64.getDecoder().decode(operation.getSourcePathKey()), StandardCharsets.UTF_8);
        String targetPath = new String(Base64.getDecoder().decode(operation.getTargetPathKey()), StandardCharsets.UTF_8);
        executor.execute(new FileComparatorTask(operationId, sourcePath, targetPath, new FileComparatorCallback() {
            @Override
            public void onStart(Long id,Integer totalFiles, Double totalsize) {
                if(!operation.getId().equals(id)) {
                    return;
                }
                operation.setTotalSize(totalsize);
                operation.setStarted(1);
                operation.setTotalFiles(totalFiles);
            }

            @Override
            public void onNextCompare(FileOperationDto fileOperation) {
                logger.info("Comparing: {} -> {} : {}", fileOperation.sourcePath(), fileOperation.targetPath(), fileOperation.operation());
                StepModel step = new StepModel();
                step.setId(UUID.randomUUID().toString());
                step.setOperation(operation);
                step.setSourcePath(fileOperation.sourcePath());
                step.setTargetPath(fileOperation.targetPath());
                step.setOperationType(fileOperation.operation());
                step.setSize(fileOperation.fileSize());
                step.setFinished(0);
                final Double finishedSize = atomicSizeSum.getOrDefault(operationId, 0.0) + fileOperation.fileSize();
                atomicSizeSum.put(operationId, finishedSize);
                operation.setFinishedSize(finishedSize);
                final Integer totalScannedFiles = atomicScannedFiles.getOrDefault(operationId, 0)+1;
                atomicScannedFiles.put(operationId, totalScannedFiles);
                operation.setTotalScannedFiles(totalScannedFiles);
                operationRepository.save(operation);
                stepRepository.save(step);

            }

            @Override
            public void onFinish(List<FileOperationDto> fileOperations) {
                operation.setReady(1);
                operation.setTotalScannedFiles(fileOperations.size());
                operationRepository.save(operation);
                operationRepository.flush();
                logger.info("Finished comparing itens: {}", fileOperations.size());
            }
        }, fast
        ));

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
