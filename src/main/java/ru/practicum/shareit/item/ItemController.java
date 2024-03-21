package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.addNewItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> add(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(itemService.getOwnerItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        return ResponseEntity.ok().body(itemService.search(userId, text));
    }
}
