package org.lemanoman.filesyncserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "operation")
public class OperationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sourcePathKey;

    @Column
    private String targetPathKey;

    @OneToOne(cascade = CascadeType.ALL)
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
}
