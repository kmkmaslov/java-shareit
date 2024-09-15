package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> add(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @RequestBody BookingFromUserDto bookingFromUser) {
        log.info("POST /bookings user id={}", userId);
        return ResponseEntity.ok().body(bookingService.addNewBooking(userId, bookingFromUser));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam String approved) {
        log.info("PATCH /bookings/{} user id={}", bookingId, userId);
        return ResponseEntity.ok().body(bookingService.updateBooking(userId, bookingId, approved));

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> get(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId) {
        log.info("GET /bookings/{} user id={}", userId, bookingId);
        return ResponseEntity.ok().body(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={} user id={}", state, userId);
        return ResponseEntity.ok().body(bookingService.getUserBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getItemsOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={} user id={}", state, userId);
        return ResponseEntity.ok().body(bookingService.getItemsOwnerBookings(userId, state));
    }
}
