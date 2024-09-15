package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Transactional
public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    @Transactional(readOnly = true)
    ItemDto getItem(long userId, long itemId);

    @Transactional(readOnly = true)
    List<ItemDto> getOwnerItems(long userId);


    List<ItemDto> search(long userId, String text);

    @Transactional(readOnly = true)
    Item findItem(long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

}