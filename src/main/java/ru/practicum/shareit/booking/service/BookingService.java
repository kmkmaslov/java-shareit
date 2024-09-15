package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;

import java.util.List;

@Transactional
public interface BookingService {

    BookingDto addNewBooking(long userId, BookingFromUserDto bookingFromUser);

    BookingDto updateBooking(long userId, long bookingId, String approved) throws RuntimeException;

    @Transactional(readOnly = true)
    BookingDto getBooking(long userId, long bookingId);

    @Transactional(readOnly = true)
    List<BookingDto> getUserBookings(long userId, String state);

    @Transactional(readOnly = true)
    List<BookingDto> getItemsOwnerBookings(long userId, String state);

}