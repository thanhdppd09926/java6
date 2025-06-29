package ass.ass.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ass.ass.dao.ProductDao;
import ass.ass.models.Accounts;
import ass.ass.models.Categories;
import ass.ass.models.Products;
import ass.ass.repository.CategoryRepository;
import ass.ass.repository.ProductRepository;
import ass.ass.services.OrderService;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        List<Products> products = productRepository.findAll();
        Collections.shuffle(products);
        model.addAttribute("productsHome", products);
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        Accounts user = (Accounts) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println("Home user: " + user.getUsername());
        } else {
            System.out.println("No user in session");
        }
        return "index/home";
    }

    @GetMapping("/home/newbio")
    public String getMethodName(Model model) {

        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "index/gioithieu";
    }

    @GetMapping("/home-product")
    public String homeProduct(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model,
            HttpSession session) {
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        Pageable pageable = PageRequest.of(page, size);
        Page<Products> productPage = productRepository.findAll(pageable);

        model.addAttribute("productsHome", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", size); // để Thymeleaf sinh đúng đường dẫn

        Accounts user = (Accounts) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "index/home-product";
    }

    @GetMapping("/product")
    public String product() {
        return "index/product_detail";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Products product = productDao.findById(id).orElse(null);
        if (product == null) {
            return "error/404";
        }
        model.addAttribute("product", product);

        List<Products> relatedProducts = productDao.findByCategoryId(product.getCategory().getId());
        relatedProducts.removeIf(p -> p.getId() == id);
        if (relatedProducts.size() > 3) {
            relatedProducts = relatedProducts.subList(0, 3);
        }
        model.addAttribute("relatedProducts", relatedProducts);

        return "index/product-detail"; // Trả về template chi tiết sản phẩm
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        // Tìm kiếm sản phẩm theo tên (hoặc các tiêu chí khác nếu cần)
        List<Products> searchResults = productDao.findByNameContainingIgnoreCase(keyword); // Cần thêm phương thức này
                                                                                           // trong
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        // ProductDao
        model.addAttribute("productsHome", searchResults);
        model.addAttribute("keyword", keyword); // Để hiển thị từ khóa trên giao diện nếu cần
        return "index/home"; // Tái sử dụng template home hoặc tạo template mới như "index/search-results"
    }

    @PostMapping("/buy-now")
    public String buyNow(
            @RequestParam String username,
            @RequestParam int productId,
            @RequestParam int quantity,
            @RequestParam String address,
            HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || !user.getUsername().equals(username)) {
            return "redirect:/login";
        }

        try {
            orderService.buyNow(username, productId, quantity, address);
            return "redirect:/orders?username=" + username;
        } catch (Exception e) {
            System.out.println("Error during buy now: " + e.getMessage());
            return "redirect:/home?error=buy_failed";
        }
    }

}