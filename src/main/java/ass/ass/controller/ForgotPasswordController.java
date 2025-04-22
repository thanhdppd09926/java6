package ass.ass.controller;

import ass.ass.models.Accounts;
import ass.ass.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/nopass")
    public String showForgotPasswordForm(Model model, HttpSession session) {
        return "index/forgot-password"; // Trả về form quên mật khẩu
    }

    @PostMapping("/nopass")
    public String handleForgotPassword(@RequestParam("email") String email, Model model, HttpSession session) {
        // Kiểm tra email trong database
        Accounts account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "Email không tồn tại!");
            return "index/forgot-password";
        }

        // Tạo mật khẩu mới
        String newPassword = UUID.randomUUID().toString().substring(0, 8); // Mật khẩu ngẫu nhiên 8 ký tự
        account.setPassword(newPassword);
        accountRepository.save(account); // Cập nhật mật khẩu mới vào database

        // Gửi email với mật khẩu mới
        String messageBody = "Chào " + account.getFullname() + ",\n\n" +
                "Mật khẩu mới của bạn là: " + newPassword + "\n" +
                "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
                "Trân trọng,\nĐội ngũ hỗ trợ";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Đặt lại mật khẩu");
            message.setText(messageBody);
            message.setFrom("your-email@gmail.com"); // Thay bằng email của bạn
            mailSender.send(message);

            model.addAttribute("success", "Mật khẩu mới đã được gửi tới email của bạn!");
        } catch (Exception e) {
            model.addAttribute("error", "Không thể gửi email, vui lòng thử lại!");
            e.printStackTrace();
            return "index/nopass";
        }

        return "redirect:/login"; // Trả về form với thông báo
    }

    // Phương thức tiện ích để tìm account theo email (tùy chọn nếu repository chưa
    // có)
    private Accounts findByEmail(String email) {
        return accountRepository.findAll().stream()
                .filter(acc -> acc.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}