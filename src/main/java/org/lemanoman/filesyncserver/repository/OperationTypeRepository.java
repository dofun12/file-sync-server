package org.lemanoman.filesyncserver.repository;


import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.model.OperationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<OperationModel, Long> {
}
