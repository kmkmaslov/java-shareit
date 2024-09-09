package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;
    private final UserService userService;
    @Autowired

    private BookingMapper bookingMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public BookingDto addNewBooking(long userId, BookingFromUserDto bookingFromUser) {
        return null;
    }

    @Override
    public BookingDto updateBooking(long userId, long bookingId, String approved) throws RuntimeException {
        return null;
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String state) {
        return null;
    }

    @Override
    public List<BookingDto> getItemsOwnerBookings(long userId, String state) {
        return null;
    }
}