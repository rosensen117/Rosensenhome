package com.shiguang.lostfound.item;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shiguang.lostfound.item.ItemDtos.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) { this.itemService = itemService; }

    @PostMapping
    public ItemResponse create(Authentication authentication, @Valid @RequestBody CreateItemRequest request) {
        return itemService.create(authentication.getName(), request);
    }

    @GetMapping
    public List<ItemResponse> list() { return itemService.findAll(); }

    @GetMapping("/mine")
    public List<ItemResponse> mine(Authentication authentication) {
        return itemService.findMine(authentication.getName());
    }

    @GetMapping("/{id}")
    public ItemResponse detail(@PathVariable Long id) { return itemService.findById(id); }
}
