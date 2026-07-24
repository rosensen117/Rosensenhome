package com.shiguang.lostfound.item;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DraftMapper {
    int insert(ItemDraft draft);
    int update(ItemDraft draft);
    ItemDraft findOwned(@Param("id") Long id, @Param("userId") Long userId);
    List<ItemDraft> findByUserId(@Param("userId") Long userId);
    int deleteOwned(@Param("id") Long id, @Param("userId") Long userId);
}
