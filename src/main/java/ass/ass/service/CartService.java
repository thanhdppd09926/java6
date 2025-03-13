// package ass.ass.service;

// import ass.ass.models.Accounts;
// import ass.ass.models.Cart;
// import ass.ass.models.Cart.CartStatus;
// import ass.ass.models.Products;
// import ass.ass.repository.CartRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.util.List;
// import java.util.Optional;

// @Service
// public class CartService {

//     @Autowired
//     private CartRepository cartRepository;

//     // Thêm sản phẩm vào giỏ hàng
//     public Cart addToCart(Accounts user, Products product, Integer quantity) {
//         // Kiểm tra sản phẩm đã tồn tại trong giỏ chưa
//         Optional<Cart> existingCart = cartRepository.findByUserUsernameAndProductId(user.getUsername(), product.getId());
        
//         if (existingCart.isPresent()) {
//             Cart cart = existingCart.get();
//             cart.setQuantity(cart.getQuantity() + (quantity != null ? quantity : 1)); // Tăng số lượng
//             return cartRepository.save(cart);
//         } else {
//             Cart cart = Cart.builder()
//                     .user(user)
//                     .product(product)
//                     .quantity(quantity != null ? quantity : 1)
//                     .status(CartStatus.PENDING)
//                     .build();
//             return cartRepository.save(cart);
//         }
//     }

//     // Lấy tất cả các mục trong giỏ hàng của một người dùng
//     public List<Cart> getCartItems(String username) {
//         return cartRepository.findByUserUsername(username);
//     }

//     // Cập nhật số lượng sản phẩm trong giỏ hàng
//     public Cart updateQuantity(Long cartId, Integer quantity) {
//         Optional<Cart> cartOptional = cartRepository.findById(cartId);
//         if (cartOptional.isPresent()) {
//             Cart cart = cartOptional.get();
//             cart.setQuantity(quantity);
//             return cartRepository.save(cart);
//         }
//         throw new RuntimeException("Không tìm thấy mục trong giỏ hàng với ID: " + cartId);
//     }

//     // Xóa một mục khỏi giỏ hàng
//     public void removeFromCart(Long cartId) {
//         cartRepository.deleteById(cartId);
//     }

//     // Xóa tất cả các mục trong giỏ hàng của người dùng
//     public void clearCart(String username) {
//         List<Cart> cartItems = cartRepository.findByUserUsername(username);
//         cartRepository.deleteAll(cartItems);
//     }

//     // Cập nhật trạng thái giỏ hàng (ví dụ: từ PENDING sang CHECKED_OUT)
//     public Cart updateCartStatus(Long cartId, CartStatus status) {
//         Optional<Cart> cartOptional = cartRepository.findById(cartId);
//         if (cartOptional.isPresent()) {
//             Cart cart = cartOptional.get();
//             cart.setStatus(status);
//             return cartRepository.save(cart);
//         }
//         throw new RuntimeException("Không tìm thấy mục trong giỏ hàng với ID: " + cartId);
//     }
// }