package com.template.springboot.repository;

import com.template.springboot.model.Category;
import com.template.springboot.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ItemRepository integration tests")
class ItemRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private ItemRepository itemRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = categoryRepository.save(Category.builder().name("Electronics").build());
    }

    @Test
    @DisplayName("findByCategoryId returns correct items")
    void findByCategoryId_returnsItems() {
        itemRepository.save(Item.builder().name("Laptop").price(new BigDecimal("999")).category(electronics).build());
        itemRepository.save(Item.builder().name("Phone").price(new BigDecimal("500")).category(electronics).build());

        Page<Item> page = itemRepository.findByCategoryId(electronics.getId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).extracting(Item::getName).containsExactlyInAnyOrder("Laptop", "Phone");
    }

    @Test
    @DisplayName("countByCategoryId returns correct count")
    void countByCategoryId_returnsCount() {
        itemRepository.save(Item.builder().name("Laptop").price(new BigDecimal("999")).category(electronics).build());

        long count = itemRepository.countByCategoryId(electronics.getId());

        assertThat(count).isEqualTo(1);
    }
}
