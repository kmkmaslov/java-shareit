package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingFromUserDto {

    private Long itemId;
    private LocalDate start;
    private LocalDate end;

}
