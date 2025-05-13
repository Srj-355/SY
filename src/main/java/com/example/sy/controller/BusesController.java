package com.example.sy.controller;

import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import com.example.sy.service.BookingService;
import com.example.sy.service.BusService;
import com.example.sy.util.DynamicPricing;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BusesController {
    private final BusService busService;
    private final BookingService bookingService;
    private final String HOLIDAY_FILE_PATH = "src/main/resources/static/js/Holiday-list.json";
    private static final int TOTAL_SEATS = 36;
    private static final int MIN_MINUTES_BEFORE_DEPARTURE = 30;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public BusesController(BusService busService,BookingService bookingService) {
        this.busService = busService;
        this.bookingService = bookingService;
    }
    List<String> cities = Arrays.asList(
        "Bhojpur", "Pokhara", "Birgunj", "Lumbini", "Dharan",
        "Hetuda", "Butwal", "Biratnagar", "Janakpur", "Dhankuta",
        "Ilam", "Jhapa", "Khotang", "Morang", "Okhaldhunga",
        "Pachthar", "Sankhuwasabha", "Solukhumbu", "Sunsari", "Taplejung",
        "Terhathum", "Udayapur", "Parsa", "Bara", "Rautahat",
        "Sarlahi", "Siraha", "Dhanusha", "Saptari", "Mahottari",
        "Bhaktapur", "Chitwan", "Dhading", "Dolakha", "Kathmandu",
        "Kavrepalanchok", "Lalitpur", "Makwanpur", "Nuwakot", "Ramechap",
        "Rasuwa", "Sindhuli", "Sindhupalchok", "Baglung", "Gorkha",
        "Kaski", "Lamjung", "Manang", "Mustang", "Myagdi",
        "Nawalpur", "Parwat", "Syangja", "Tanahun", "Kapilvastu",
        "Parasi", "Rupandehi", "Arghakhanchi", "Gulmi", "Palpa",
        "Dang", "Pyuthan", "Rolpa", "Eastern Rukum", "Banke",
        "Bardiya", "Western Rukum", "Salyan", "Dolpa", "Humla",
        "Jumla", "Kalikot", "Mugu", "Surkhet", "Dailekh",
        "Jajarkot", "Darchula", "Bajhang", "Bajura", "Baitadi",
        "Doti", "Acham", "Dadeldhura", "Kanchanpur", "Kailali",
        "Darjeling"
);

// @GetMapping("/buses")
//     public String showBuses(
//             @RequestParam String departure,
//             @RequestParam String destination,
//             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//             Model model) {

//         String formattedDeparture = departure.substring(0, 1).toUpperCase() + departure.substring(1);
//         String formattedDestination = destination.substring(0, 1).toUpperCase() + destination.substring(1);

//         List<Bus> buses = busService.findByRouteAndIsActive(departure.toLowerCase(), destination.toLowerCase(), true);
//         buses = buses.stream()
//         .filter(bus -> {
//             // Check if departure is at least 30 minutes from now
//             LocalTime departureTime = LocalTime.parse(bus.getDepartureTime());
//             LocalDateTime departureDateTime = LocalDateTime.of(date, departureTime);
//             LocalDateTime now = LocalDateTime.now();
            
//             return departureDateTime.isAfter(now.plusMinutes(MIN_MINUTES_BEFORE_DEPARTURE));
//         })
//         .filter(bus -> {
//             // Check seat availability
//             List<Booking> bookings = bookingService.findConfirmedBookingsByBusAndDate(
//                 bus, date.toString());
//             int bookedSeats = bookings.stream()
//                 .mapToInt(b -> b.getSeats().split(",").length)
//                 .sum();
//             return bookedSeats < TOTAL_SEATS;
//         })
//         .collect(Collectors.toList());


//         // Apply dynamic pricing
//         Map<Long, Double> adjustedFares = new HashMap<>();
//         DynamicPricing pricing = new DynamicPricing(HOLIDAY_FILE_PATH, 0);

//         for (Bus bus : buses) {
//             pricing.setBaseFare(bus.getFare());
//             adjustedFares.put(bus.getId(), pricing.getFare(date.toString()));
//         }

//         model.addAttribute("buses", buses);
//         model.addAttribute("adjustedFares", adjustedFares);
//         model.addAttribute("departure", formattedDeparture);
//         model.addAttribute("destination", formattedDestination);
//         model.addAttribute("date", date);
//         model.addAttribute("cities", cities);

//         return "buses";
//     }

//@GetMapping("/buses")
//    public String showBuses(
//            @RequestParam String departure,
//            @RequestParam String destination,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            Model model) {
//
//        String formattedDeparture = departure.substring(0, 1).toUpperCase() + departure.substring(1);
//        String formattedDestination = destination.substring(0, 1).toUpperCase() + destination.substring(1);
//
//        List<Bus> buses = busService.findByRouteAndIsActive(departure.toLowerCase(), destination.toLowerCase(), true);
//
//        buses = buses.stream()
//            .filter(bus -> {
//                try {
//                    LocalTime departureTime = LocalTime.parse(bus.getDepartureTime(), TIME_FORMATTER);
//                    LocalDateTime departureDateTime = LocalDateTime.of(date, departureTime);
//                    return departureDateTime.isAfter(LocalDateTime.now().plusMinutes(MIN_MINUTES_BEFORE_DEPARTURE));
//                } catch (Exception e) {
//                    return false;
//                }
//            })
//            .filter(bus -> {
//                List<Booking> bookings = bookingService.findConfirmedBookingsByBusAndDate(bus, date.toString());
//                int bookedSeats = bookings.stream()
//                    .mapToInt(b -> b.getSeats().split(",").length)
//                    .sum();
//                return bookedSeats < TOTAL_SEATS;
//            })
//            .collect(Collectors.toList());
//
//        // Apply dynamic pricing
//        Map<Long, Double> adjustedFares = new HashMap<>();
//
//
//        DynamicPricing pricing = new DynamicPricing(HOLIDAY_FILE_PATH, 0);
//        Map<Long, List<String>> bookedSeatsMap = new HashMap<>();
//        for (Bus bus : buses) {
//            pricing.setBaseFare(bus.getFare());
//            adjustedFares.put(bus.getId(), pricing.getFare(date.toString()));
//
//            List<Booking> bookings = bookingService.findConfirmedBookingsByBusAndDate(bus, date.toString());
//            List<String> bookedSeats = bookings.stream()
//                .flatMap(b -> Arrays.stream(b.getSeats().split(",")))
//                .collect(Collectors.toList());
//            bookedSeatsMap.put(bus.getId(), bookedSeats);
//        }
//
//        model.addAttribute("buses", buses);
//        model.addAttribute("adjustedFares", adjustedFares);
//        model.addAttribute("bookedSeats", bookedSeatsMap);
//        model.addAttribute("departure", formattedDeparture);
//        model.addAttribute("destination", formattedDestination);
//        model.addAttribute("date", date);
//        model.addAttribute("cities", cities);
//
//        return "buses";
//    }

    @GetMapping("/buses")
    public String showBuses(
            @RequestParam String departure,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        String formattedDeparture = departure.substring(0, 1).toUpperCase() + departure.substring(1);
        String formattedDestination = destination.substring(0, 1).toUpperCase() + destination.substring(1);

        List<Bus> buses = busService.findByRouteAndIsActive(departure.toLowerCase(), destination.toLowerCase(), true);

        buses = buses.stream()
                .filter(bus -> {
                    try {
                        LocalTime departureTime = LocalTime.parse(bus.getDepartureTime(), TIME_FORMATTER);
                        LocalDateTime departureDateTime = LocalDateTime.of(date, departureTime);
                        return departureDateTime.isAfter(LocalDateTime.now().plusMinutes(MIN_MINUTES_BEFORE_DEPARTURE));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(bus -> {
                    List<Booking> bookings = bookingService.findConfirmedBookingsByBusAndDate(bus, date.toString());
                    int bookedSeats = bookings.stream()
                            .mapToInt(b -> b.getSeats().split(",").length)
                            .sum();
                    return bookedSeats < TOTAL_SEATS;
                })
                .collect(Collectors.toList());

        // Apply enhanced dynamic pricing
        Map<Long, Double> adjustedFares = new HashMap<>();
        Map<Long, List<String>> bookedSeatsMap = new HashMap<>();
        DynamicPricing pricing = new DynamicPricing(HOLIDAY_FILE_PATH, TOTAL_SEATS);

        for (Bus bus : buses) {
            pricing.setBaseFare(bus.getFare());

            // Get booked seats count for this bus
            List<Booking> bookings = bookingService.    findConfirmedBookingsByBusAndDate(bus, date.toString());
            int bookedSeats = bookings.stream()
                    .mapToInt(b -> b.getSeats().split(",").length)
                    .sum();
            pricing.setBookedSeats(bookedSeats);

            // Calculate dynamic price
            adjustedFares.put(bus.getId(), pricing.getFare(date.toString()));

            // Track booked seats for seat selection
            List<String> bookedSeatNumbers = bookings.stream()
                    .flatMap(b -> Arrays.stream(b.getSeats().split(",")))
                    .collect(Collectors.toList());
            bookedSeatsMap.put(bus.getId(), bookedSeatNumbers);
        }

        model.addAttribute("buses", buses);
        model.addAttribute("adjustedFares", adjustedFares);
        model.addAttribute("bookedSeats", bookedSeatsMap);
        model.addAttribute("departure", formattedDeparture);
        model.addAttribute("destination", formattedDestination);
        model.addAttribute("date", date);
        model.addAttribute("cities", cities);

        return "buses";
    }

    @PostMapping("/search")
    public String searchBuses(
            @RequestParam String departure,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return "redirect:/buses?departure=" + departure +
                "&destination=" + destination +
                "&date=" + date;
    }


@PostMapping("/buses/book")
public String handleBookingForm(
        @RequestParam Long busId,
        @RequestParam String seats,
        @RequestParam String boardingPoint,
        @RequestParam double totalAmount,
        @RequestParam String departure,
        @RequestParam String destination,
        @RequestParam String departureTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpSession session,
        @AuthenticationPrincipal User principal,
        RedirectAttributes redirectAttributes) {

    if (principal == null) {
        // Store all booking details in session
        Map<String, Object> pendingBooking = new HashMap<>();
        pendingBooking.put("busId", busId);
        pendingBooking.put("seats", seats);
        pendingBooking.put("boardingPoint", boardingPoint);
        pendingBooking.put("totalAmount", totalAmount);
        pendingBooking.put("departure", departure);
        pendingBooking.put("departureTime", departureTime);
        pendingBooking.put("arrival", destination);
        pendingBooking.put("date", date.toString());
        session.setAttribute("pendingBooking", pendingBooking);

        String bookingUrl = "/booking?busId=" + busId +
                "&seats=" + URLEncoder.encode(seats, StandardCharsets.UTF_8) +
                "&boardingPoint=" + URLEncoder.encode(boardingPoint, StandardCharsets.UTF_8) +
                "&totalAmount=" + totalAmount +
                "&date=" + date+
                "&departure=" + URLEncoder.encode(departure, StandardCharsets.UTF_8) +
                "&arrival=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                "&departureTime=" + URLEncoder.encode(departureTime, StandardCharsets.UTF_8);

        redirectAttributes.addAttribute("redirect", bookingUrl);
        return "redirect:/auth?form=login&booking=true";
    }

    // If already logged in, proceed directly to booking
    return "redirect:/booking?busId=" + busId +
            "&seats=" + URLEncoder.encode(seats, StandardCharsets.UTF_8) +
            "&boardingPoint=" + URLEncoder.encode(boardingPoint, StandardCharsets.UTF_8) +
            "&totalAmount=" + totalAmount +
            "&departure=" + URLEncoder.encode(departure, StandardCharsets.UTF_8) +
            "&arrival=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
            "&date=" + date;
}

}