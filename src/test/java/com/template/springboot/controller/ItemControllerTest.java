package com.template.springboot.controller;

import com.template.springboot.config.SecurityConfig;
import com.template.springboot.dto.ItemDto;
import com.template.springboot.service.CategoryService;
import com.template.springboot.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(SecurityConfig.class)
@DisplayName("ItemController WebMvc tests")
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ItemService itemService;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private com.template.springboot.security.CustomUserDetailsService userDetailsService;
    @MockitoBean private com.template.springboot.security.CustomOAuth2UserService oAuth2UserService;

    @Test
    @DisplayName("GET /items is accessible to authenticated users")
    @WithMockUser
    void list_authenticated_returnsOk() throws Exception {
        given(itemService.findAll(any())).willReturn(new PageImpl<>(List.of()));
        given(categoryService.findAll(any())).willReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("item/list"));
    }

    @Test
    @DisplayName("POST /items saves item and redirects")
    @WithMockUser
    void create_valid_redirects() throws Exception {
        mockMvc.perform(post("/items")
                        .with(csrf())
                        .param("name", "Laptop")
                        .param("price", "999.99")
                        .param("categoryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }
}
