package com.shiguang.lostfound.item;

import com.shiguang.lostfound.auth.ApiException;
import com.shiguang.lostfound.auth.UserAccount;
import com.shiguang.lostfound.auth.UserAccountMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.shiguang.lostfound.item.ItemDtos.*;

@Service
public class ItemService {
    private final ItemMapper itemMapper;
    private final UserAccountMapper userMapper;

    public ItemService(ItemMapper itemMapper, UserAccountMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public ItemResponse create(String studentId, CreateItemRequest request) {
        UserAccount user = userMapper.findByStudentId(studentId);
        if (user == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录");

        ItemPost post = new ItemPost();
        post.setUserId(user.getId());
        post.setType(request.type());
        post.setTitle(request.title().trim());
        post.setCategory(request.category().trim());
        post.setEventDate(request.eventDate());
        post.setLocation(request.location().trim());
        post.setDescription(request.description().trim());
        post.setHiddenFeature(trimToNull(request.hiddenFeature()));
        post.setStatus("PUBLISHED");
        post.setCreatedAt(Instant.now());
        itemMapper.insertPost(post);

        List<ImageRequest> images = request.images() == null ? List.of() : request.images();
        for (int index = 0; index < images.size(); index++) {
            ImageRequest source = images.get(index);
            ItemImage image = new ItemImage();
            image.setItemId(post.getId());
            image.setObjectKey(source.key());
            image.setUrl(source.url());
            image.setSortOrder(index);
            itemMapper.insertImage(image);
        }
        return findById(post.getId());
    }

    public List<ItemResponse> findAll() {
        return itemMapper.findPublishedPosts().stream().map(this::toResponse).toList();
    }

    public List<ItemResponse> findMine(String studentId) {
        UserAccount user = requireUser(studentId);
        return itemMapper.findPostsByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    public List<ItemResponse> findFavorites(String studentId) {
        UserAccount user = requireUser(studentId);
        return itemMapper.findFavoritePostsByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void addFavorite(String studentId, Long itemId) {
        UserAccount user = requireUser(studentId);
        findById(itemId);
        if (!itemMapper.favoriteExists(user.getId(), itemId)) {
            itemMapper.insertFavorite(user.getId(), itemId, Instant.now());
        }
    }

    @Transactional
    public void removeFavorite(String studentId, Long itemId) {
        UserAccount user = requireUser(studentId);
        itemMapper.deleteFavorite(user.getId(), itemId);
    }

    public ItemResponse findById(Long id) {
        ItemPost post = itemMapper.findPostById(id);
        if (post == null) throw new ApiException(HttpStatus.NOT_FOUND, "没有找到这条失物信息");
        return toResponse(post);
    }

    private ItemResponse toResponse(ItemPost post) {
        List<ImageResponse> images = itemMapper.findImagesByItemId(post.getId()).stream()
                .map(image -> new ImageResponse(image.getObjectKey(), image.getUrl(), image.getSortOrder()))
                .toList();
        return new ItemResponse(post.getId(), post.getType(), post.getTitle(), post.getCategory(),
                post.getEventDate(), post.getLocation(), post.getDescription(), post.getStatus(),
                post.getCreatedAt(), post.getPublisherName(), post.isPublisherVerified(), images);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private UserAccount requireUser(String studentId) {
        UserAccount user = userMapper.findByStudentId(studentId);
        if (user == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录");
        return user;
    }
}
