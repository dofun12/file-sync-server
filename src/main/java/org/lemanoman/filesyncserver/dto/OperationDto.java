package org.lemanoman.filesyncserver.dto;

public class OperationDto {
    private String sourcePathkey;
    private String targetPathkey;
    private Long operationTypeId;

    public String getSourcePathkey() {
        return sourcePathkey;
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

    public Long getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        this.operationTypeId = operationTypeId;
    }
}
