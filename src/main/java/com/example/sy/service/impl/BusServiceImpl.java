package com.example.sy.service.impl;

import com.example.sy.model.Bus;
import com.example.sy.repository.BusRepository;
import com.example.sy.service.BusService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;


    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    @Override
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }
    @Override
    public Page<Bus> findByBusNumber(String busNumber, Pageable pageable) {
        return busRepository.findByBusNumber(busNumber, pageable);
    }
    @Override
    public Bus getBusById(Long id) {
        return busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));
    }

    @Override
    public Bus updateBus(Bus bus, Long id) {
        Bus existingBus = getBusById(id);
        existingBus.setBusName(bus.getBusName());
        existingBus.setBusNumber(bus.getBusNumber());
        existingBus.setBusType(bus.getBusType());
        existingBus.setOperatorName(bus.getOperatorName());
        existingBus.setDepartureTime(bus.getDepartureTime());
        existingBus.setDepartureStation(bus.getDepartureStation());
        existingBus.setArrivalTime(bus.getArrivalTime());
        existingBus.setArrivalStation(bus.getArrivalStation());
        existingBus.setFare(bus.getFare());
        existingBus.setIsActive(bus.getIsActive());
        return busRepository.save(existingBus);
    }

    @Override
    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        busRepository.delete(bus);
    }

    @Override
    public List<Bus> findByBusNumber(String busNumber) {
        return busRepository.findByBusNumber(busNumber);
    }

    @Override
    public List<Bus> findByBusType(String busType) {
        return busRepository.findByBusType(busType);
    }

    @Override
    public List<Bus> findByRoute(String departureStation, String arrivalStation) {
        return busRepository.findByRouteIgnoreCase(departureStation, arrivalStation);
    }

    @Override
    public List<Bus> findByRouteAndIsActive(String departureStation, String arrivalStation, boolean isActive) {
        return busRepository.findByRouteAndIsActiveIgnoreCase(departureStation, arrivalStation, isActive);
    }

    @Override
    public List<Bus> findActiveBuses() {
        return busRepository.findByIsActiveTrue();
    }

    @Override
    public Page<Bus> getAllBuses(Pageable pageable) {
        return busRepository.findAll(pageable);
    }


}