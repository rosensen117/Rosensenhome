package com.shiguang.lostfound;

import com.shiguang.lostfound.auth.AuthDtos.RegisterRequest;
import com.shiguang.lostfound.auth.AuthService;
import com.shiguang.lostfound.item.ItemDtos.CreateItemRequest;
import com.shiguang.lostfound.item.DraftDtos.SaveDraftRequest;
import com.shiguang.lostfound.item.DraftService;
import com.shiguang.lostfound.item.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemServiceTests {
    @Autowired AuthService authService;
    @Autowired ItemService itemService;
    @Autowired DraftService draftService;
    @Autowired JdbcTemplate jdbcTemplate;

    @AfterEach
    void removeTestRecords() {
        jdbcTemplate.update("DELETE FROM item_drafts WHERE user_id IN (SELECT id FROM users WHERE name = ?)", "发布测试用户");
        jdbcTemplate.update("DELETE FROM item_favorites WHERE item_id IN (SELECT id FROM item_posts WHERE title = ?)", "测试校园卡");
        jdbcTemplate.update("DELETE FROM item_images WHERE item_id IN (SELECT id FROM item_posts WHERE title = ?)", "测试校园卡");
        jdbcTemplate.update("DELETE FROM item_posts WHERE title = ?", "测试校园卡");
        jdbcTemplate.update("DELETE FROM auth_tokens WHERE user_id IN (SELECT id FROM users WHERE name = ?)", "发布测试用户");
        jdbcTemplate.update("DELETE FROM users WHERE name = ? AND id NOT IN (SELECT user_id FROM item_posts)", "发布测试用户");
    }

    @Test
    void authenticatedUserCanPublishAndReadItem() {
        String suffix = Long.toString(System.nanoTime()).substring(5);
        String studentId = "P" + suffix.substring(0, Math.min(10, suffix.length()));
        String phone = "136" + suffix.substring(0, 8);
        authService.register(new RegisterRequest("发布测试用户", studentId, phone, null, "Test@123456"));

        var created = itemService.create(studentId, new CreateItemRequest(
                "lost", "测试校园卡", "证件卡片", LocalDate.now(), "图书馆三层",
                "蓝色卡套，背面有贴纸", "卡套内部特征", List.of()));

        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("测试校园卡");
        assertThat(itemService.findById(created.id()).location()).isEqualTo("图书馆三层");
        assertThat(itemService.findAll()).extracting("id").contains(created.id());
        assertThat(itemService.findMine(studentId)).extracting("id").containsExactly(created.id());
        itemService.addFavorite(studentId, created.id());
        itemService.addFavorite(studentId, created.id());
        assertThat(itemService.findFavorites(studentId)).extracting("id").containsExactly(created.id());
        itemService.removeFavorite(studentId, created.id());
        assertThat(itemService.findFavorites(studentId)).isEmpty();

        var draft = draftService.save(studentId, null, new SaveDraftRequest(
                "found", "", "随身物品", null, "食堂", "", null, List.of()));
        assertThat(draft.id()).isNotNull();
        assertThat(draft.eventDate()).isNull();
        assertThat(draftService.findAll(studentId)).extracting("id").contains(draft.id());
        draftService.delete(studentId, draft.id());
        assertThat(draftService.findAll(studentId)).isEmpty();
    }
}
