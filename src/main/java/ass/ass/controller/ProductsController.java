package ass.ass.controller;

import ass.ass.dao.CategoryDao;
import ass.ass.dao.ProductDao;
import ass.ass.models.Categories;
import ass.ass.models.Products;
import ass.ass.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

@Controller
@RequestMapping("/products/admin")
public class ProductsController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("")
    public String getAllProducts(Model model) {
        List<Products> products = productDao.findAll();
        model.addAttribute("products", products);
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/products";
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Products());
        return "admin/add_product";
    }

    @PostMapping("/new")
    public String addProduct(@ModelAttribute Products product,
                             @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String cleanName = removeAccentsAndSpaces(product.getName());
                String originalFilename = imageFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = System.currentTimeMillis() + "_" + cleanName + extension;

                String imagePath = "D:\\Java6_github\\java6\\src\\main\\resources\\static\\photos\\" + newFileName;
                imageFile.transferTo(new File(imagePath));
                product.setImage(newFileName);
            } catch (IOException e) {
                System.out.println("LỖI: " + e.getMessage());
            }
        }
        productDao.save(product);
        return "redirect:/products/admin";
    }

    @GetMapping("/{id}")
    public String showEditProductForm(@PathVariable int id, Model model) {
        Products product = productDao.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products/admin";
        }
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        return "admin/edit_product";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable int id,
                                @ModelAttribute Products product,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Products existingProduct = productDao.findById(id).orElse(null);
        if (existingProduct != null) {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String cleanName = removeAccentsAndSpaces(product.getName());
                    String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
                    String newFileName = System.currentTimeMillis() + "_" + cleanName + extension;
                    String imagePath = "D:\\Java6_github\\java6\\src\\main\\resources\\static\\photos\\" + newFileName;
                    imageFile.transferTo(new File(imagePath));
                    product.setImage(newFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setImage(existingProduct.getImage());
            }
            product.setId(id);
            productDao.save(product);
        }
        return "redirect:/products/admin";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable int id) {
        productDao.deleteById(id);
        return "redirect:/products/admin";
    }

    private String removeAccentsAndSpaces(String str) {
        if (str == null) return "";
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("\\s+", "_").toLowerCase();
    }
}
