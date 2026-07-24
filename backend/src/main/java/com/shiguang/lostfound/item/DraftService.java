package com.shiguang.lostfound.item;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.shiguang.lostfound.auth.ApiException;
import com.shiguang.lostfound.auth.UserAccount;
import com.shiguang.lostfound.auth.UserAccountMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.shiguang.lostfound.item.DraftDtos.*;
import static com.shiguang.lostfound.item.ItemDtos.*;

@Service
public class DraftService {
    private final DraftMapper draftMapper;
    private final UserAccountMapper userMapper;
    private final ObjectMapper objectMapper;

    public DraftService(DraftMapper draftMapper, UserAccountMapper userMapper, ObjectMapper objectMapper) {
        this.draftMapper = draftMapper;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    public List<DraftResponse> findAll(String studentId) {
        return draftMapper.findByUserId(requireUser(studentId).getId()).stream().map(this::toResponse).toList();
    }

    public DraftResponse findById(String studentId, Long id) {
        ItemDraft draft = draftMapper.findOwned(id, requireUser(studentId).getId());
        if (draft == null) throw new ApiException(HttpStatus.NOT_FOUND, "没有找到这份草稿");
        return toResponse(draft);
    }

    @Transactional
    public DraftResponse save(String studentId, Long id, SaveDraftRequest request) {
        UserAccount user = requireUser(studentId);
        ItemDraft draft;
        if (id == null) {
            draft = new ItemDraft();
            draft.setUserId(user.getId());
            draft.setCreatedAt(Instant.now());
        } else {
            draft = draftMapper.findOwned(id, user.getId());
            if (draft == null) throw new ApiException(HttpStatus.NOT_FOUND, "没有找到这份草稿");
        }
        draft.setType(request.type() == null ? "lost" : request.type());
        draft.setTitle(trimToNull(request.title()));
        draft.setCategory(trimToNull(request.category()));
        draft.setEventDate(request.eventDate());
        draft.setLocation(trimToNull(request.location()));
        draft.setDescription(trimToNull(request.description()));
        draft.setHiddenFeature(trimToNull(request.hiddenFeature()));
        draft.setImagesJson(writeImages(request.images()));
        draft.setUpdatedAt(Instant.now());
        if (id == null) draftMapper.insert(draft); else draftMapper.update(draft);
        return toResponse(draft);
    }

    @Transactional
    public void delete(String studentId, Long id) {
        draftMapper.deleteOwned(id, requireUser(studentId).getId());
    }

    private DraftResponse toResponse(ItemDraft draft) {
        List<ImageResponse> images = readImages(draft.getImagesJson()).stream()
                .map(image -> new ImageResponse(image.key(), image.url(), 0)).toList();
        return new DraftResponse(draft.getId(), draft.getType(), draft.getTitle(), draft.getCategory(),
                draft.getEventDate(), draft.getLocation(), draft.getDescription(), draft.getHiddenFeature(),
                images, draft.getCreatedAt(), draft.getUpdatedAt());
    }

    private String writeImages(List<ImageRequest> images) {
        try { return objectMapper.writeValueAsString(images == null ? List.of() : images); }
        catch (JacksonException exception) { throw new ApiException(HttpStatus.BAD_REQUEST, "草稿图片信息格式不正确"); }
    }

    private List<ImageRequest> readImages(String json) {
        if (json == null || json.isBlank()) return List.of();
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JacksonException exception) { return List.of(); }
    }

    private UserAccount requireUser(String studentId) {
        UserAccount user = userMapper.findByStudentId(studentId);
        if (user == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录");
        return user;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
