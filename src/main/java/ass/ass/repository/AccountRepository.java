package ass.ass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ass.ass.models.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, String> {
    Accounts findByUsername(String username);
    Accounts findByEmail(String email); // Thêm phương thức này
}