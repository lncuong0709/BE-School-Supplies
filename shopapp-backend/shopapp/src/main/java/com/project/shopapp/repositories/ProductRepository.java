package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:productName%")
    List<Product> findProductsByName(@Param("productName") String name);
    Page<Product> findAll(Pageable pageable);//phân trang


    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") long categoryId);


    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT MIN(p1.id) FROM Product p1 GROUP BY p1.category.id)")
    List<Product> findDistinctImageByCategory();
}
