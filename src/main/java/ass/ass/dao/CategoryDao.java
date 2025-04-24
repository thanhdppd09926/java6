package ass.ass.dao;

import ass.ass.models.Categories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Categories, Integer> {
    List<Categories> findAll();
}