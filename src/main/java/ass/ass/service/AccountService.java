package ass.ass.service;

import ass.ass.models.Accounts;
import ass.ass.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Accounts getAccountByUsername(String username) {
        return accountRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với username: " + username));
    }

    // (Tùy chọn) Thêm các phương thức khác nếu cần, ví dụ:
    public Accounts saveAccount(Accounts account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(String username) {
        accountRepository.deleteById(username);
    }
}