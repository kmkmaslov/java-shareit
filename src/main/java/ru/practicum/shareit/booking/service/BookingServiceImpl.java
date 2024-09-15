package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFromUserDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        UserDto userDto = userService.getUser(userId);
        validateBookingDate(userId, bookingFromUser);
        ItemDto itemDto = getValidatedBookingItem(userId, bookingFromUser);
        LocalDate start = bookingFromUser.getStart();
        LocalDate end = bookingFromUser.getEnd();
        Booking booking = Booking.builder()
                .booker(userMapper.toUser(userDto))
                .item(itemMapper.toItem(itemDto))
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        log.info("booking {}  user id={}", savedBooking, userId);
        return bookingMapper.toBookingDto(savedBooking);
    }

    private void validateBookingDate(long userId, BookingFromUserDto booking) {
        LocalDate start = booking.getStart();
        LocalDate end = booking.getEnd();
        if (start == null || end == null) {
            log.info("booking {} from id={} does not have date", booking, userId);
            throw new ValidationException();
        }
        if (start.equals(end)) {
            log.info("booking {} from id={} start and end date are equals", booking, userId);
            throw new ValidationException();
        }
        if (start.isAfter(end)) {
            log.info("booking {} from id={} end date before start ", booking, userId);
            throw new ValidationException();
        }
        if (start.isBefore(LocalDate.now())) {
            log.info("booking {} from id={} start date before now", booking, userId);
            throw new ValidationException();
        }
        if (end.isBefore(LocalDate.now())) {
            log.info("У бронирования {} от user id={} дата окончания бронирования раньше текущей даты", booking, userId);
            throw new ValidationException();
        }
    }

    private ItemDto getValidatedBookingItem(long userId, BookingFromUserDto booking) {
        Long itemId = booking.getItemId();
        if (itemId == null) {
            log.info("booking {} from user id={} has wrong itemId ={}", booking, userId, null);
            throw new NotFoundException();
        }
        ItemDto item = itemService.getItem(userId, itemId);
        if (Boolean.FALSE.equals(item.getAvailable())) {
            log.info("booking {} from user id={} has wrong itemId={}", booking, userId, booking.getItemId());
            throw new ValidationException();
        }
        long ownerId = itemService.findItem(itemId).getOwner().getId();
        if (ownerId == userId) {
            log.info("booking {} from user id={} try to book own item with itemId={}", booking, userId, booking.getItemId());
            throw new NotFoundException();
        }
        return itemService.getItem(userId, itemId);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(long userId, long bookingId, String approved) throws RuntimeException {
        Booking booking = findBooking(bookingId);

        validateBookingUpdate(userId, booking);
        switch (approved) {
            case "true":
                if (booking.getStatus().equals(Status.APPROVED)) {
                    log.info("booking {} from user id={} has status {}", booking, userId, booking.getStatus());
                    throw new ForbiddenException();
                }
                booking.setStatus(Status.APPROVED);
                break;
            case "false":
                if (booking.getStatus().equals(Status.REJECTED)) {
                    log.info("booking {} from user id={} has status {}", booking, userId, booking.getStatus());
                    throw new ValidationException();
                }
                booking.setStatus(Status.REJECTED);
                break;
            default:
                throw new RuntimeException();
        }
        Booking savedBooking = bookingRepository.save(booking);
        log.info("user id={} changes booking {}", savedBooking, userId);
        return bookingMapper.toBookingDto(savedBooking);
    }

    private Booking findBooking(long bookingId) {
        if (bookingId == 0) {
            throw new ValidationException();
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException();
        }
        return booking.get();
    }

    private void validateBookingUpdate(long userId, Booking booking) {
        UserDto userDto = userService.getUser2(userId);



        if (userDto.getId() != booking.getItem().getOwner().getId()) {
            log.info("Item {} with booking {} has owner {}, operation from user id={}",
                    booking.getItem(),
                    booking,
                    booking.getItem().getOwner(),
                    userDto.getId());
            throw new ForbiddenException();
        }
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        User user = userMapper.toUser(userService.getUser(userId));
        Booking booking = findBooking(bookingId);
        if (!(user.equals(booking.getBooker()) || user.equals(booking.getItem().getOwner()))) {
            log.info("Different user id={}, booker id={} or owner id={}",
                    user.getId(),
                    booking.getBooker().getId(),
                    booking.getItem().getOwner().getId());
            throw new ForbiddenException();
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String state) {
        User user = userMapper.toUser(userService.getUser(userId));
        State status;
        try {
            status = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException();
        }
        List<Booking> bookings = new ArrayList<>();
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(user, Sort.by("end").descending());
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(
                        user,
                        LocalDate.now(),
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerAndEndIsBefore(
                        user,
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerAndStartIsAfter(
                        user,
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatusIs(
                        user,
                        Status.WAITING,
                        Sort.by("end").descending());
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatusIs(
                        user,
                        Status.REJECTED,
                        Sort.by("end").descending());
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getItemsOwnerBookings(long userId, String state) {
        User user = userMapper.toUser(userService.getUser(userId));
        State status;
        try {
            status = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException();
        }
        List<Booking> bookings = new ArrayList<>();
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIs(user, Sort.by("end").descending());
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(
                        user,
                        LocalDate.now(),
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndIsBefore(
                        user,
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartIsAfter(
                        user,
                        LocalDate.now(),
                        Sort.by("end").descending());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusIs(
                        user,
                        Status.WAITING,
                        Sort.by("end").descending());
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusIs(
                        user,
                        Status.REJECTED,
                        Sort.by("end").descending());
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}