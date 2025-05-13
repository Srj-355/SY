package com.example.sy.controller;

import com.example.sy.model.Bus;
import com.example.sy.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus) {
        Bus savedBus = busService.saveBus(bus);
        return new ResponseEntity<>(savedBus, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {
        List<Bus> buses = busService.getAllBuses();
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        Bus bus = busService.getBusById(id);
        return new ResponseEntity<>(bus, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @RequestBody Bus bus) {
        Bus updatedBus = busService.updateBus(bus, id);
        return new ResponseEntity<>(updatedBus, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search/number/{busNumber}")
    public ResponseEntity<List<Bus>> getBusesByNumber(@PathVariable String busNumber) {
        List<Bus> buses = busService.findByBusNumber(busNumber);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    @GetMapping("/search/type/{busType}")
    public ResponseEntity<List<Bus>> getBusesByType(@PathVariable String busType) {
        List<Bus> buses = busService.findByBusType(busType);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    @GetMapping("/search/route")
    public ResponseEntity<List<Bus>> getBusesByRoute(@RequestParam String departure, @RequestParam String arrival) {
        List<Bus> buses = busService.findByRoute(departure, arrival);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Bus>> getActiveBuses() {
        List<Bus> buses = busService.findActiveBuses();
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }


}