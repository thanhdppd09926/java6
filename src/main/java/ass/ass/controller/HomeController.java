package ass.ass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ass.ass.dao.ProductDao;
import ass.ass.models.Products;
import ass.ass.repository.ProductRepository;
import ass.ass.service.ProductService;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductService productsService;

    @GetMapping("/home")
    public String home(Model model) {
        List<Products> productsHome = productDao.findAll();
        model.addAttribute("productsHome", productsHome);
        return "index/home";
    }

    @GetMapping("/product")
    public String product() {
        return "index/product_detail";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Products product = productDao.findById(id).orElse(null);

        model.addAttribute("product", product);

       
        return "index/product-detail"; // Trả về template chi tiết sản phẩm
    }
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword , Model model) {
        List<Products> productssearch = productDao.findByNameContainingIgnoreCase(keyword);
        model.addAttribute("productssearch", productssearch);
        model.addAttribute("keyword", keyword);
        return "index/home";
    }
    @GetMapping("/category/{categoryId}")
    public String getProductsByCategory(@PathVariable String categoryId, Model model) {
        List<Products> products = productsService.getProductsByCategoryId(categoryId);
        model.addAttribute("products", products);
        return "index/type"; // Tên view (HTML) sẽ là products.html
    }
    
}