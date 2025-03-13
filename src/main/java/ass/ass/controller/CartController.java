// package ass.ass.controller;

// import ass.ass.models.Accounts;
// import ass.ass.models.Cart;
// import ass.ass.models.Products;
// import ass.ass.service.AccountService;
// import ass.ass.service.CartService;
// import ass.ass.service.ProductService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/cart")
// public class CartController {

//     @Autowired
//     private CartService cartService;

//     @Autowired
//     private AccountService accountService;

//     @Autowired
//     private ProductService productService;

//     // Thêm sản phẩm vào giỏ hàng
//     @PostMapping("/add")
//     public ResponseEntity<Cart> addToCart(
//             @RequestParam String username,
//             @RequestParam Integer productId, // Sử dụng Integer thay vì Long để khớp với entity Products
//             @RequestParam(required = false) Integer quantity) {
        
//         // Lấy thông tin người dùng từ username
//         Accounts user = accountService.getAccountByUsername(username);
        
//         // Lấy thông tin sản phẩm từ productId
//         Products product = productService.getProductById(productId);
        
//         // Thêm vào giỏ hàng
//         Cart cart = cartService.addToCart(user, product, quantity);
//         return ResponseEntity.ok(cart);
//     }

//     // Lấy tất cả các mục trong giỏ hàng của người dùng
//     @GetMapping("/items")
//     public ResponseEntity<List<Cart>> getCartItems(@RequestParam String username) {
//         List<Cart> cartItems = cartService.getCartItems(username);
//         return ResponseEntity.ok(cartItems);
//     }

//     // Cập nhật số lượng sản phẩm trong giỏ hàng
//     @PutMapping("/update/quantity/{cartId}")
//     public ResponseEntity<Cart> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
//         Cart updatedCart = cartService.updateQuantity(cartId, quantity);
//         return ResponseEntity.ok(updatedCart);
//     }

//     // Xóa một mục khỏi giỏ hàng
//     @DeleteMapping("/remove/{cartId}")
//     public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId) {
//         cartService.removeFromCart(cartId);
//         return ResponseEntity.ok().build();
//     }

//     // Xóa toàn bộ giỏ hàng của người dùng
//     @DeleteMapping("/clear")
//     public ResponseEntity<Void> clearCart(@RequestParam String username) {
//         cartService.clearCart(username);
//         return ResponseEntity.ok().build();
//     }

//     // Cập nhật trạng thái giỏ hàng
//     @PutMapping("/update/status/{cartId}")
//     public ResponseEntity<Cart> updateCartStatus(@PathVariable Long cartId, @RequestParam Cart.CartStatus status) {
//         Cart updatedCart = cartService.updateCartStatus(cartId, status);
//         return ResponseEntity.ok(updatedCart);
//     }
// }