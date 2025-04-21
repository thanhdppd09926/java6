package ass.ass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ass.ass.services.ZalopayService;

import java.util.Map;

@RestController
@RequestMapping("/api/zalopay")
public class ZalopayController {

    @Autowired
    private ZalopayService zalopayService;

    @PostMapping
public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> orderRequest) {
    try {
        Map<String, Object> response = zalopayService.createOrder(orderRequest); // Trả về JSON map
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        Map<String, Object> error = Map.of("error", "Error creating payment: " + e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}


    @GetMapping("/order-status/{appTransId}")
    public ResponseEntity<String> getOrderStatus(@PathVariable String appTransId) {
        String response = zalopayService.getOrderStatus(appTransId);
        return ResponseEntity.ok(response);
    }

}
