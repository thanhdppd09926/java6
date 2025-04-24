package ass.ass.controller;

import ass.ass.models.Accounts;
import ass.ass.models.Orders;
import ass.ass.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewOrders(@RequestParam String username, Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || !user.getUsername().equals(username)) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getOrdersByUsername(username));
        model.addAttribute("username", username);
        return "index/orders";
    }

    @GetMapping("/details/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        Accounts user = (Accounts) session.getAttribute("user");
        if (user == null || user.isAdmin()) {
            return "redirect:/login";
        }

        // Lấy thông tin đơn hàng và chi tiết đơn hàng
        Orders order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderService.getOrderDetailsByOrderId(orderId));
        return "index/order_details"; // Trả về template admin/order-details.html
    }
}