package ass.ass.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ass.ass.dao.ProductDao;
import ass.ass.models.Products;
import ass.ass.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductRepository productsRepository;

    public List<Products> getProductsByCategoryId(String categoryId) {
        return productsRepository.findByCategory_Id(categoryId);
    }
    public List<Products> getAllProducts() {
        return productDao.findAll();
    }

    public Products getProductById(int id) {
        return productDao.findById(id).orElse(null);
    }

    public Products saveProduct(Products product) {
        return productDao.save(product);
    }

    public void deleteProduct(int id) {
        productDao.deleteById(id);
    }
}