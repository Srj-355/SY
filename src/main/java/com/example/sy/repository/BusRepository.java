package com.example.sy.repository;

import com.example.sy.model.Bus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByBusNumber(String busNumber);
    List<Bus> findByBusType(String busType);

    @Query("SELECT b FROM Bus b WHERE LOWER(b.departureStation) = LOWER(:departureStation) " +
            "AND LOWER(b.arrivalStation) = LOWER(:arrivalStation)")
    List<Bus> findByRouteIgnoreCase(@Param("departureStation") String departureStation,
                                    @Param("arrivalStation") String arrivalStation);

    @Query("SELECT b FROM Bus b WHERE LOWER(b.departureStation) = LOWER(:departureStation) " +
            "AND LOWER(b.arrivalStation) = LOWER(:arrivalStation) AND b.isActive = :isActive")
    List<Bus> findByRouteAndIsActiveIgnoreCase(@Param("departureStation") String departureStation,
                                               @Param("arrivalStation") String arrivalStation,
                                               @Param("isActive") boolean isActive);

    List<Bus> findByIsActiveTrue();
    Page<Bus> findByBusNumber(String busNumber, Pageable pageable);
    long countByIsActive(boolean b);
}