package org.lemanoman.filesyncserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "step")
public class StepModel {
    @Id
    private String id;

    @Column
    private String sourcePath;

    @Column
    private String targetPath;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id", referencedColumnName = "id")
    private OperationModel operation;

    @Column
    private String operationType;

    private int destinationExists;

    @Column
    private String sourceMd5sum;

    @Column
    private String targetMd5sum;

    @Column(name = "status_message", length = 255)
    private String statusMessage;

    @Column
    private Long size;

    @Column
    private int finished=0;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public void setId(String id) {
        this.id = id;
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

    public OperationModel getOperation() {
        return operation;
    }

    public void setOperation(OperationModel operation) {
        this.operation = operation;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getDestinationExists() {
        return destinationExists;
    }

    public void setDestinationExists(int destinationExists) {
        this.destinationExists = destinationExists;
    }

    public String getSourceMd5sum() {
        return sourceMd5sum;
    }

    public void setSourceMd5sum(String sourceMd5sum) {
        this.sourceMd5sum = sourceMd5sum;
    }

    public String getTargetMd5sum() {
        return targetMd5sum;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setTargetMd5sum(String targetMd5sum) {
        this.targetMd5sum = targetMd5sum;
    }
}
