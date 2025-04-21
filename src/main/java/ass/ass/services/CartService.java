package ass.ass.services;

import ass.ass.models.Accounts;
import ass.ass.models.Cart;
import ass.ass.models.Products;
import ass.ass.repository.AccountRepository;
import ass.ass.repository.CartRepository;
import ass.ass.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Xem giỏ hàng của người dùng
    public List<Cart> viewCart(String username) {
        Accounts account = accountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại!"));
        return cartRepository.findByAccount(account);
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(String username, int productId, int quantity) {
        Accounts account = accountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại!"));
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));

        if (!product.isAvailable()) {
            throw new IllegalStateException("Sản phẩm không có sẵn!");
        }

        Cart existingItem = cartRepository.findByAccountAndProductId(account, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartRepository.save(existingItem);
        } else {
            Cart newItem = Cart.builder()
                    .account(account)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cartRepository.save(newItem);
        }
        System.out.println("Product added to cart successfully for user: " + username);
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateCart(int productId, int quantity) {
        Cart item = cartRepository.findByProductId(productId);  // Giả sử có phương thức này trong CartRepository

        if (item != null) {
            if (quantity <= 0) {
                cartRepository.delete(item);  // Xóa sản phẩm nếu quantity <= 0
            } else {
                item.setQuantity(quantity);  // Cập nhật số lượng sản phẩm
                cartRepository.save(item);
            }
        } else {
            throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng!");
        }
    }

    // Xóa toàn bộ giỏ hàng theo username (gọi khi checkout xong)
    public void clearCart(String username) {
        Accounts account = accountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại!"));
        List<Cart> cartItems = cartRepository.findByAccount(account);
        cartRepository.deleteAll(cartItems);
        System.out.println("Giỏ hàng đã được xóa cho người dùng: " + username);
    }
    public void checkoutAndClearCart(String username) {
        Accounts account = accountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại!"));
        
        List<Cart> cartItems = cartRepository.findByAccount(account);  // Lấy sản phẩm trong giỏ hàng của user
        if (!cartItems.isEmpty()) {
            cartRepository.deleteAll(cartItems);  // Xóa tất cả sản phẩm trong giỏ hàng
        }
    }
    
}
