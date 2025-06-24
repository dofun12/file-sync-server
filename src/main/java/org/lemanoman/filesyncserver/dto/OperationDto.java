package org.lemanoman.filesyncserver.dto;

import org.lemanoman.filesyncserver.FileUtils;
import org.lemanoman.filesyncserver.model.OperationModel;

import java.util.Objects;

public class OperationDto {
    private Long id;
    private String sourcePath;
    private String targetPath;
    private String sourcePathkey;
    private String targetPathkey;
    private String operationName;
    private boolean ready;
    private boolean started;
    private boolean running;
    private Integer totalFiles;
    private Integer totalScannedFiles;
    private String humanTotalSize;
    private boolean finished;
    private String progressScan;

    public OperationDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationDto(OperationModel operationModel) {
        this.id = operationModel.getId();
        this.sourcePath = FileUtils.fromBase64(operationModel.getSourcePathKey());
        this.targetPath = FileUtils.fromBase64(operationModel.getTargetPathKey());
        this.sourcePathkey = operationModel.getSourcePathKey();
        this.targetPathkey = operationModel.getTargetPathKey();
        this.operationName = operationModel.getOperationType().getName();
        this.started = Objects.equals(operationModel.getStarted(), 1);
        this.totalFiles = operationModel.getTotalFiles();
        this.totalScannedFiles = operationModel.getTotalScannedFiles();
        this.humanTotalSize = FileUtils.bytesToHumanReadable(operationModel.getTotalSize());
        this.finished = Objects.equals(operationModel.getFinished(), 1);
        this.running = Objects.equals(operationModel.getRunning(), 1);
        this.ready = Objects.equals(operationModel.getReady(), 1);
        this.progressScan = ((double) operationModel.getTotalScannedFiles()/(double) operationModel.getTotalFiles()) * 100 + "%";
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getSourcePathkey() {
        return sourcePathkey;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setSourcePathkey(String sourcePathkey) {
        this.sourcePathkey = sourcePathkey;
    }

    public String getTargetPathkey() {
        return targetPathkey;
    }

    public void setTargetPathkey(String targetPathkey) {
        this.targetPathkey = targetPathkey;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Integer getTotalScannedFiles() {
        return totalScannedFiles;
    }

    public void setTotalScannedFiles(Integer totalScannedFiles) {
        this.totalScannedFiles = totalScannedFiles;
    }

    public String getHumanTotalSize() {
        return humanTotalSize;
    }

    public void setHumanTotalSize(String humanTotalSize) {
        this.humanTotalSize = humanTotalSize;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getProgressScan() {
        return progressScan;
    }

    public void setProgressScan(String progressScan) {
        this.progressScan = progressScan;
    }
}
