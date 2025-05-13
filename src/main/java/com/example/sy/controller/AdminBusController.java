package com.example.sy.controller;

import com.example.sy.annotation.AdminAccessControl;
import com.example.sy.model.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.sy.service.BusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@AdminAccessControl
@Controller
@RequestMapping("/admin/buses")
public class AdminBusController {
    private final BusService busService;

    public AdminBusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
public String allBuses(Model model,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      @RequestParam(defaultValue = "id") String sort,
                      @RequestParam(defaultValue = "asc") String direction,
                      @RequestParam(required = false) String busNumber) {
    Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sort));
    
    Page<Bus> busesPage;
    if (busNumber != null && !busNumber.isEmpty()) {
        busesPage = busService.findByBusNumber(busNumber, pageable);
    } else {
        busesPage = busService.getAllBuses(pageable);
    }
        model.addAttribute("buses", busesPage.getContent());
        model.addAttribute("currentPage", busesPage.getNumber() + 1);
        model.addAttribute("totalPages", busesPage.getTotalPages());
        model.addAttribute("totalItems", busesPage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "admin/buses/all-buses";
    }

    @GetMapping("/add")
    public String addBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        return "admin/buses/add-bus";
    }

    @PostMapping("/save")
    public String saveBus(@ModelAttribute Bus bus) {
        busService.saveBus(bus);
        return "redirect:/admin/buses";
    }

    @GetMapping("/edit/{id}")
    public String editBusForm(@PathVariable Long id, Model model) {
        model.addAttribute("bus", busService.getBusById(id));
        return "admin/buses/edit-bus";
    }

    @PostMapping("/update/{id}")
    public String updateBus(@PathVariable Long id, @ModelAttribute Bus bus) {
        busService.updateBus(bus, id);
        return "redirect:/admin/buses";
    }

    @GetMapping("/delete/{id}")
    public String deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return "redirect:/admin/buses";
    }
}