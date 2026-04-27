package com.template.springboot.repository;

import com.template.springboot.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("CategoryRepository integration tests")
class CategoryRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("save and findById roundtrip")
    void saveAndFind_roundtrip() {
        Category saved = categoryRepository.save(
                Category.builder().name("Books").description("All books").build());

        assertThat(saved.getId()).isNotNull();
        assertThat(categoryRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns true for existing name")
    void existsByName_caseInsensitive() {
        categoryRepository.save(Category.builder().name("Tech").build());

        assertThat(categoryRepository.existsByNameIgnoreCase("tech")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("TECH")).isTrue();
    }

    @Test
    @DisplayName("findAllByOrderByNameAsc returns sorted page")
    void findAll_sortedByName() {
        categoryRepository.save(Category.builder().name("Zephyr").build());
        categoryRepository.save(Category.builder().name("Alpha").build());

        var page = categoryRepository.findAllByOrderByNameAsc(PageRequest.of(0, 10));

        assertThat(page.getContent().get(0).getName()).isEqualTo("Alpha");
    }
}
