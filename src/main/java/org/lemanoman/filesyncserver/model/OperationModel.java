package org.lemanoman.filesyncserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "operation")
public class OperationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "operation_generator", sequenceName = "operation_seq", allocationSize = 1)
    private Long id;

    @Column
    private String sourcePathKey;

    @Column
    private Integer started = 0;

    @Column
    private Integer totalFiles=0;

    @Column
    private Integer totalScannedFiles=0;

    @Column
    private Integer finished = 0;

    @Column
    private Double totalSize = 0d;

    @Column
    private Double finishedSize = 0d;



    @Column
    private String targetPathKey;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_type_id", referencedColumnName = "id")
    private OperationTypeModel operationType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourcePathKey() {
        return sourcePathKey;
    }

    public void setSourcePathKey(String sourcePathKey) {
        this.sourcePathKey = sourcePathKey;
    }

    public String getTargetPathKey() {
        return targetPathKey;
    }

    public void setTargetPathKey(String targetPathKey) {
        this.targetPathKey = targetPathKey;
    }

    public OperationTypeModel getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeModel operationType) {
        this.operationType = operationType;
    }

    public Integer getStarted() {
        return started;
    }

    public void setStarted(Integer started) {
        this.started = started;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Double totalSize) {
        this.totalSize = totalSize;
    }

    public Double getFinishedSize() {
        return finishedSize;
    }

    public void setFinishedSize(Double finishedSize) {
        this.finishedSize = finishedSize;
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
}
