package ass.ass.controller;

import ass.ass.dao.CategoryDao;
import ass.ass.dao.ProductDao;
import ass.ass.models.Accounts;
import ass.ass.models.Categories;
import ass.ass.models.Products;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/admin/products") // Đặt mapping chung để dễ quản lý
public class ProductsController {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryDao categoryDao;

    // Lấy tất cả sản phẩm và hiển thị trang quản lý sản phẩm
    @GetMapping("")
    public String getAllProducts(Model model) {
        List<Products> products = productDao.findAll();
        model.addAttribute("products", products);
        return "admin/products"; // Trả về template HTML
    }
    //
    @GetMapping("/profile")
    public String getprofile(Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null) {
            
        }
        model.addAttribute("account", user);
            return "index/profile";
    }
    
    // Hiển thị form thêm sản phẩm
    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Products());
        return "admin/add_product"; // Trả về template form thêm sản phẩm
    }

    // Thêm sản phẩm mới
    @PostMapping("/new")
    public String addProduct(@ModelAttribute Products product,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            String imagePath = "C:/java5/ass/src/main/resources/static/images/"
                    + imageFile.getOriginalFilename();
            try {
                imageFile.transferTo(new File(imagePath));
                product.setImage("" + imageFile.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                // Có thể thêm xử lý lỗi nếu cần
            }
        }
        productDao.save(product);
        return "redirect:/admin/products";
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/{id}")
    public String showEditProductForm(@PathVariable int id, Model model) {
        Products product = productDao.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products"; // Nếu không tìm thấy sản phẩm
        }
        List<Categories> categories = categoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        return "admin/edit_product"; // Trang sửa sản phẩm
    }

    // Cập nhật sản phẩm
    @PostMapping("/{id}")
    public String updateProduct(@PathVariable int id,
            @ModelAttribute Products product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Products existingProduct = productDao.findById(id).orElse(null);
        if (existingProduct != null) {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = "C:/java5/ass/src/main/resources/static/images/"
                        + imageFile.getOriginalFilename();
                try {
                    imageFile.transferTo(new File(imagePath));
                    product.setImage("" + imageFile.getOriginalFilename());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                product.setImage(existingProduct.getImage()); // Giữ hình ảnh cũ
            }
            product.setId(id); // Đảm bảo ID không bị thay đổi
            productDao.save(product);
        }
        return "redirect:/admin/products";
    }

    // Xóa sản phẩm
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable int id) {
        productDao.deleteById(id);
        return "redirect:/admin/products";
    }
}