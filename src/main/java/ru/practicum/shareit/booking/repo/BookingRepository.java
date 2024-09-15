package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDate start, LocalDate end, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDate end, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDate start, Sort sort);

    List<Booking> findByBookerAndStatusIs(User booker, Status status, Sort sort);

    List<Booking> findAllByItemOwnerIs(User booker, Sort sort);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(User booker, LocalDate start, LocalDate end, Sort sort);

    List<Booking> findByItemOwnerAndEndIsBefore(User booker, LocalDate end, Sort sort);

    List<Booking> findByItemOwnerAndStartIsAfter(User booker, LocalDate start, Sort sort);

    List<Booking> findByItemOwnerAndStatusIs(User booker, Status status, Sort sort);

    Optional<Booking> findFirst1ByItemIdIsAndStartIsBeforeAndStatusIsOrderByEndDesc(long itemId, LocalDate end, Status status);

    Optional<Booking> findFirst1ByItemIdIsAndStartIsAfterAndStatusIsOrderByStartAsc(long itemId, LocalDate start, Status status);

    Optional<Booking> findFirst1ByItemIdAndBookerIdAndEndIsBefore(long itemId, long bookerId, LocalDate end);

}