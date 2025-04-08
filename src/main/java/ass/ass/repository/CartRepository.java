package ass.ass.repository;

import ass.ass.models.Accounts;
import ass.ass.models.Cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // Tìm giỏ hàng của một tài khoản
    List<Cart> findByAccount(Accounts account);

    // Tìm sản phẩm trong giỏ hàng bằng productId
    Cart findByProductId(int productId);

    // Tìm giỏ hàng của tài khoản và sản phẩm cụ thể
    Cart findByAccountAndProductId(Accounts account, int productId);

    // Xóa sản phẩm khỏi giỏ hàng của người dùng theo account và productId
    void deleteByAccountAndProductId(Accounts account, int productId);

    // Nếu bạn cần phương thức tìm tất cả các sản phẩm trong giỏ hàng của tài khoản
    List<Cart> findAllByAccount(Accounts account);
    
}
