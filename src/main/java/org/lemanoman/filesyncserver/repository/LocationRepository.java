package org.lemanoman.filesyncserver.repository;


import org.lemanoman.filesyncserver.model.LocationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationModel, Long> {
}
