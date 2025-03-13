package ass.ass.repository;

import ass.ass.models.Products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Products, Integer> {
    List<Products> findByCategory_Id(String categoryId);
}   