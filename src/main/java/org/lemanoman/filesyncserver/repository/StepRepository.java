package org.lemanoman.filesyncserver.repository;


import org.lemanoman.filesyncserver.model.LocationModel;
import org.lemanoman.filesyncserver.model.StepModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<StepModel, String> {
    List<StepModel> findAllByOperation_Id(Long id);
}
