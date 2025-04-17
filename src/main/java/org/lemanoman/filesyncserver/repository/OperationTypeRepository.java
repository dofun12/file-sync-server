package org.lemanoman.filesyncserver.repository;


import org.lemanoman.filesyncserver.model.OperationTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationTypeRepository extends JpaRepository<OperationTypeModel, Long> {
}
