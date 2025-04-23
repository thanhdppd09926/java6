package ass.ass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ass.ass.dao.AccountDao;
import ass.ass.models.Accounts;
import ass.ass.models.Categories;
import ass.ass.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    // Đường dẫn lưu ảnh
    private static final String UPLOAD_DIR = "D:\\Java6_github\\java6\\src\\main\\resources\\static\\photos\\";

    // Hiển thị danh sách tài khoản
    @GetMapping("/admin")
    public String getAllAccounts(Model model) {
        List<Accounts> accounts = accountDao.findAll();
        model.addAttribute("accounts", accounts);
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/accounts";
    }

    // Hiển thị form thêm tài khoản
    @GetMapping("/new")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Accounts());
        return "admin/accounts";
    }

    // Xử lý thêm tài khoản
    @PostMapping("/admin")
    public String addAccount(@ModelAttribute Accounts account, @RequestParam("imageFile") MultipartFile imageFile,
            Model model) throws IOException {
        if (!imageFile.isEmpty()) {
            String username = account.getUsername();
            String fileName = username + ".jpg"; // Hoặc .png nếu cần
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            account.setPhoto(fileName); // Lưu tên file
        }

        if (accountDao.existsById(account.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "admin/accounts";
        }

        accountDao.save(account);
        return "redirect:/accounts/admin";
    }

    // Hiển thị form sửa tài khoản
    @GetMapping("/edit/{username}")
    public String showEditAccountForm(@PathVariable String username, Model model) {
        Accounts account = accountDao.findById(username).orElse(null);
        if (account == null) {
            return "redirect:/accounts/admin";
        }
        model.addAttribute("account", account);
        return "admin/accounts";
    }

    // Xử lý sửa tài khoản
    @PostMapping("/admin/update")
    public String updateAccount(@ModelAttribute Accounts account, @RequestParam("imageFile") MultipartFile imageFile,
            Model model) throws IOException {
        if (!accountDao.existsById(account.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập không tồn tại!");
            return "admin/accounts";
        }
        saveImage(account, imageFile);
        accountDao.save(account);
        return "redirect:/accounts/admin";
    }

    // Xử lý xóa tài khoản
    @PostMapping("/admin/delete")
    public String deleteAccount(@RequestParam String username) {
        accountDao.deleteById(username);
        return "redirect:/accounts/admin";
    }

    // Hiển thị trang hồ sơ cá nhân
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        List<Categories> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("editMode", false);
        model.addAttribute("account", user);
        return "index/profile";
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

    // Xử lý cập nhật thông tin hồ sơ
    @PostMapping("/edit")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String fullname,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile imageFile,
            // @RequestParam boolean activated,
            // @RequestParam boolean admin,
            HttpSession session) throws IOException {
        Accounts account = accountDao.findById(username).orElse(null);
        if (account != null) {
            String oldEmail = account.getEmail();
            boolean emailChanged = !oldEmail.equals(email);

            account.setFullname(fullname);
            account.setEmail(email);
            if (password != null && !password.isEmpty()) {
                account.setPassword(password); // Nên mã hóa mật khẩu nếu cần
            }
            saveImage(account, imageFile);
            // account.setActivated(activated);
            // account.setAdmin(admin);

            accountDao.save(account);

            // Cập nhật session
            session.setAttribute("user", account);

            // Gửi email nếu email thay đổi
            if (emailChanged) {
                sendEmailNotification(email, fullname);
            }
        }
        return "redirect:/accounts/profile";
    }

    // Phương thức lưu ảnh
    private void saveImage(Accounts account, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Tạo tên file duy nhất để tránh trùng lặp
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR + fileName);

            // Đảm bảo thư mục tồn tại
            Files.createDirectories(uploadPath.getParent());

            // Lưu file vào thư mục
            Files.copy(imageFile.getInputStream(), uploadPath);

            // Cập nhật trường photo trong account
            account.setPhoto(fileName);
            logger.info("Image saved successfully: {}", fileName);
        } else {
            logger.warn("No image file uploaded for account: {}", account.getUsername());
        }
    }

    // Phương thức gửi email thông báo
    private void sendEmailNotification(String toEmail, String fullname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com"); // Thay bằng email thực tế
        message.setTo(toEmail);
        message.setSubject("Cập nhật email thành công");
        message.setText(
                "Xin chào " + fullname + ",\n\n" +
                        "Email của bạn đã được cập nhật thành công trên hệ thống OCEANS.\n" +
                        "Email mới: " + toEmail + "\n\n" +
                        "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n" +
                        "Trân trọng,\nĐội ngũ OCEANS");
        try {
            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

}