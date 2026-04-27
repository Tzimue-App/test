package com.template.springboot.controller;

import com.template.springboot.config.SecurityConfig;
import com.template.springboot.dto.CategoryDto;
import com.template.springboot.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
@DisplayName("CategoryController WebMvc tests")
class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CategoryService categoryService;
    @MockitoBean private com.template.springboot.security.CustomUserDetailsService userDetailsService;
    @MockitoBean private com.template.springboot.security.CustomOAuth2UserService oAuth2UserService;

    @Test
    @DisplayName("GET /categories is accessible to authenticated users")
    @WithMockUser
    void list_authenticated_returnsOk() throws Exception {
        given(categoryService.findAll(any())).willReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/list"));
    }

    @Test
    @DisplayName("GET /categories redirects to login for anonymous users")
    void list_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("POST /categories saves category and redirects")
    @WithMockUser
    void create_valid_redirects() throws Exception {
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .param("name", "New Category")
                        .param("description", "Desc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
    }

    @Test
    @DisplayName("POST /categories with invalid data returns form")
    @WithMockUser
    void create_invalid_returnsForm() throws Exception {
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .param("name", "") // Blank name
                        .param("description", "Desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/form"))
                .andExpect(model().hasErrors());
    }
}
