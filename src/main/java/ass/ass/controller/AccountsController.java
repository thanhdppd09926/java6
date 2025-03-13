package ass.ass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ass.ass.dao.AccountDao;
import ass.ass.models.Accounts;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountDao accountDao;

    // Hiển thị danh sách tài khoản
    @GetMapping
    public String getAllAccounts(Model model) {
        List<Accounts> accounts = accountDao.findAll();
        model.addAttribute("accounts", accounts);
        return "admin/accounts"; // Trả về view admin/accounts.html
    }

    // Hiển thị form thêm tài khoản
    @GetMapping("/new")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Accounts());
        return "admin/accounts"; // Tái sử dụng view, hoặc tạo view riêng nếu cần
    }

    // Xử lý thêm tài khoản
    @PostMapping
    public String addAccount(@ModelAttribute Accounts account) {
        accountDao.save(account);
        return "redirect:/accounts"; // Chuyển hướng về danh sách sau khi thêm
    }

    // Hiển thị form sửa tài khoản (tùy chọn, nếu cần form riêng)
    @GetMapping("/edit/{username}")
    public String showEditAccountForm(@PathVariable String username, Model model) {
        Accounts account = accountDao.findById(username).orElse(null);
        if (account == null) {
            return "redirect:/accounts"; // Nếu không tìm thấy, quay lại danh sách
        }
        model.addAttribute("account", account);
        return "admin/accounts"; // Tái sử dụng view, hoặc tạo view riêng nếu cần
    }

    // Xử lý sửa tài khoản
    @PostMapping("/update")
    public String updateAccount(@ModelAttribute Accounts account) {
        accountDao.save(account); // save() sẽ cập nhật nếu username đã tồn tại
        return "redirect:/accounts"; // Chuyển hướng về danh sách sau khi sửa
    }

    // Xử lý xóa tài khoản
    @PostMapping("/delete")
    public String deleteAccount(@RequestParam String username) {
        accountDao.deleteById(username);
        return "redirect:/accounts"; // Chuyển hướng về danh sách sau khi xóa
    }
    // Hiển thị form chỉnh sửa hồ sơ
    @GetMapping("/edit")
    public String showEditProfile(Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("editMode", true);
        model.addAttribute("account", user);
        return "index/profile";
    }

    @PostMapping("/edit")
public String updateProfile(
        @RequestParam String username,
        @RequestParam(required = false) String password,
        @RequestParam String fullname,
        @RequestParam String email,
        @RequestParam boolean activated,
        @RequestParam boolean admin,
        HttpSession session,
        RedirectAttributes redirectAttributes) {
    try {
        Accounts account = accountDao.findById(username).orElse(null);
        if (account != null) {
            String oldEmail = account.getEmail();
            boolean emailChanged = !oldEmail.equals(email);

            account.setFullname(fullname);
            account.setEmail(email);
            if (password != null && !password.isEmpty()) {
                // Consider adding password encoding here
                account.setPassword(password);
            }
            account.setActivated(activated);
            account.setAdmin(admin);

            accountDao.save(account);
            session.setAttribute("user", account);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Account not found");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/accounts/profile";
}
@GetMapping("/profile")
public String showProfile(Model model, HttpSession session) {
    Accounts user = (Accounts) session.getAttribute("user");
    if (user == null) {
        return "redirect:/login";
    }
    // Đảm bảo không ở chế độ chỉnh sửa
    model.addAttribute("editMode", false);
    model.addAttribute("account", user);
    return "index/profile";
}
}