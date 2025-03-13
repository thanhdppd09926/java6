package ass.ass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import ass.ass.models.Accounts;
import ass.ass.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("user")
public class LoginController {

    @Autowired
    private AccountRepository accountRepository; // Inject repository

    @GetMapping("/register")
    public String register(Model model, @ModelAttribute("accounts") Accounts accounts) {
        return "index/register"; // Trả về trang đăng ký
    }

    @PostMapping("/register/new")
    public String newRegister(Model model, @ModelAttribute("accounts") Accounts accounts, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("errorMessage", "Cần nhập đúng các trường dữ liệu!!");
        } else {
            accounts.setAdmin(false); // Đặt admin = 0
            accounts.setActivated(false); // Đặt activated = 0
            accountRepository.save(accounts); // Lưu tài khoản vào cơ sở dữ liệu
            model.addAttribute("message", "Đã đăng ký thành công!!");
        }
        return "index/login"; // Trả về trang đăng nhập
    }

    @GetMapping("/login")
    public String loginHome() {
        return "index/login";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String username, @RequestParam String password,
            HttpSession session) {
        Accounts account = accountRepository.findByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            model.addAttribute("user", account); // Lưu đối tượng user vào model (và session)
            session.setAttribute("user", account);
            // Kiểm tra quyền admin
            if (account.isAdmin()) {
                return "redirect:/home"; // Điều hướng đến trang admin
            } else {
                return "redirect:/home"; // Điều hướng đến trang chính cho người dùng bình thường
            }
        } else {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "index/login"; // Quay lại trang đăng nhập
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, SessionStatus status) {
        status.setComplete();
        session.invalidate();
        return "redirect:/login"; // Điều hướng về trang đăng nhập
    }
}