package com.example.sy.service;

import com.example.sy.model.Bus;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusService {
    Bus saveBus(Bus bus);

    List<Bus> getAllBuses();

    Bus getBusById(Long id);

    Bus updateBus(Bus bus, Long id);

    void deleteBus(Long id);

    Page<Bus> getAllBuses(Pageable pageable);

    List<Bus> findByBusNumber(String busNumber);

    List<Bus> findByBusType(String busType);

    List<Bus> findByRoute(String departureStation, String arrivalStation);

    List<Bus> findActiveBuses();

    Page<Bus> findByBusNumber(String busNumber, Pageable pageable);

    List<Bus> findByRouteAndIsActive(String departureStation, String arrivalStation, boolean isActive);


}