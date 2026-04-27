package com.template.springboot.repository;

import com.template.springboot.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllByOrderByNameAsc(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}
