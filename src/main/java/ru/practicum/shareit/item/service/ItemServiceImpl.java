package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validate(itemDto);
        Item item = itemMapper.toItem(itemDto);
        User owner = userMapper.toUser(userService.getUser(userId));
        item.setOwner(owner);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("In {} no name", itemDto);
            throw new ValidationException();
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("In {} no description", itemDto);
            throw new ValidationException();
        } else if (itemDto.getAvailable() == null) {
            log.info("In {} no status", itemDto);
            throw new ValidationException();
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException();
        }
        Item itemToUpdate = optionalItem.get();
        User owner = userMapper.toUser(userService.getUser(userId));
        if (!itemToUpdate.getOwner().equals(owner)) {
            throw new NotFoundException();
        }
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        Item savedItem = itemRepository.save(itemToUpdate);
        log.info("update item: {}", savedItem);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        if (itemId == 0) {
            throw new ValidationException();
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException();
        }
        Item item = optionalItem.get();
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            addBookings(itemDto);
        }
        addComments(itemDto);
        return itemDto;
    }

    private void addBookings(ItemDto itemDto) {
        long itemId = itemDto.getId();
        Optional<Booking> lastBooking = bookingRepository.findFirst1ByItemIdIsAndStartIsBeforeAndStatusIsOrderByEndDesc(
                itemId,
                LocalDate.now(),
                Status.APPROVED);
        itemDto.setLastBooking(bookingMapper.toBookingForItemDto(lastBooking.orElse(null)));
        Optional<Booking> nextBooking = bookingRepository.findFirst1ByItemIdIsAndStartIsAfterAndStatusIsOrderByStartAsc(
                itemId,
                LocalDate.now(),
                Status.APPROVED);
        itemDto.setNextBooking(bookingMapper.toBookingForItemDto(nextBooking.orElse(null)));
    }

    private void addComments(ItemDto itemDto) {
        long itemId = itemDto.getId();
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> listCommentDto = comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(listCommentDto);
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        List<Item> ownerItems = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : ownerItems) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            if (item.getOwner().getId().equals(userId)) {
                addBookings(itemDto);
            }
            addComments(itemDto);
            listItemDto.add(itemDto);
        }
        return listItemDto;
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> foundItems = itemRepository.search(text);
        return foundItems
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Item findItem(long itemId) {
        if (itemId == 0) {
            throw new ValidationException();
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException();
        }
        return item.get();
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = userMapper.toUser(userService.getUser2(userId));
        Item item = findItem(itemId);
        Optional<Booking> booking = bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDate.now());
        if (booking.isEmpty()) {
            log.info("Пользователь с id={} не может добавить отзыв к товару с id={}, он не бронировал его",
                    userId, itemId);
            throw new ValidationException();
        }
        String text = commentDto.getText();
        if (text == null || text.isBlank()) {
            log.info("В отзыве от пользователя с id={} на товар с id={} нет текста",
                    userId, itemId);
            throw new ValidationException();
        }
        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDate.now())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Сохранен новый отзыв {} от пользователя с id={} на товар с id={} текста",
                savedComment,
                userId,
                itemId);
        return commentMapper.toCommentDto(savedComment);
    }
}