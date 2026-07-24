package com.shiguang.lostfound.item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shiguang.lostfound.item.DraftDtos.*;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {
    private final DraftService draftService;

    public DraftController(DraftService draftService) { this.draftService = draftService; }

    @GetMapping
    public List<DraftResponse> list(Authentication authentication) { return draftService.findAll(authentication.getName()); }

    @GetMapping("/{id}")
    public DraftResponse detail(Authentication authentication, @PathVariable Long id) { return draftService.findById(authentication.getName(), id); }

    @PostMapping
    public DraftResponse create(Authentication authentication, @Valid @RequestBody SaveDraftRequest request) {
        return draftService.save(authentication.getName(), null, request);
    }

    @PutMapping("/{id}")
    public DraftResponse update(Authentication authentication, @PathVariable Long id, @Valid @RequestBody SaveDraftRequest request) {
        return draftService.save(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long id) { draftService.delete(authentication.getName(), id); }
}
