package com.shiguang.lostfound.item;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shiguang.lostfound.item.ItemDtos.ItemResponse;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final ItemService itemService;

    public FavoriteController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> list(Authentication authentication) {
        return itemService.findFavorites(authentication.getName());
    }

    @PutMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(Authentication authentication, @PathVariable Long itemId) {
        itemService.addFavorite(authentication.getName(), itemId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(Authentication authentication, @PathVariable Long itemId) {
        itemService.removeFavorite(authentication.getName(), itemId);
    }
}
