package com.template.springboot.seeder;

import com.template.springboot.model.Category;
import com.template.springboot.model.Item;
import com.template.springboot.model.User;
import com.template.springboot.repository.CategoryRepository;
import com.template.springboot.repository.ItemRepository;
import com.template.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("DataSeeder: database already populated, skipping.");
            return;
        }

        log.info("DataSeeder: seeding database...");

        // -- Users
        userRepository.save(User.builder()
                .email("admin@template.com")
                .password(passwordEncoder.encode("admin123"))
                .name("Admin User")
                .role(User.Role.ROLE_ADMIN)
                .provider(User.AuthProvider.LOCAL)
                .build());

        userRepository.save(User.builder()
                .email("user@template.com")
                .password(passwordEncoder.encode("user123"))
                .name("Regular User")
                .role(User.Role.ROLE_USER)
                .provider(User.AuthProvider.LOCAL)
                .build());

        Category electronics = categoryRepository.save(Category.builder()
                .name("Electronics").description("Gadgets and devices").build());

        Category books = categoryRepository.save(Category.builder()
                .name("Books").description("Physical and digital books").build());

        itemRepository.save(Item.builder().name("Laptop Pro 15")
                .description("High-performance laptop").price(new BigDecimal("1299.99"))
                .category(electronics).build());

        itemRepository.save(Item.builder().name("Wireless Headphones")
                .description("Noise-cancelling, 30h battery").price(new BigDecimal("249.99"))
                .category(electronics).build());

        itemRepository.save(Item.builder().name("Clean Code")
                .description("Robert C. Martin — best practices").price(new BigDecimal("34.99"))
                .category(books).build());

        itemRepository.save(Item.builder().name("Designing Data-Intensive Applications")
                .description("Martin Kleppmann — distributed systems").price(new BigDecimal("49.99"))
                .category(books).build());

        log.info("DataSeeder: done — 2 users, 2 categories, 4 items created.");
    }
}
