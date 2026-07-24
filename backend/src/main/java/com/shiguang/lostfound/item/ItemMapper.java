package com.shiguang.lostfound.item;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.time.Instant;

@Mapper
public interface ItemMapper {
    int insertPost(ItemPost post);
    int insertImage(ItemImage image);
    ItemPost findPostById(@Param("id") Long id);
    List<ItemPost> findPublishedPosts();
    List<ItemPost> findPostsByUserId(@Param("userId") Long userId);
    List<ItemPost> findFavoritePostsByUserId(@Param("userId") Long userId);
    int insertFavorite(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("createdAt") Instant createdAt);
    int deleteFavorite(@Param("userId") Long userId, @Param("itemId") Long itemId);
    boolean favoriteExists(@Param("userId") Long userId, @Param("itemId") Long itemId);
    List<ItemImage> findImagesByItemId(@Param("itemId") Long itemId);
}
