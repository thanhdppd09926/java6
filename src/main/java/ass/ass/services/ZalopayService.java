package ass.ass.services;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ass.ass.config.ZalopayConfig;
import ass.ass.crypto.HMACUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZalopayService {

    // Hàm lấy thời gian hiện tại theo định dạng
    private static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    // Tạo đơn hàng ZaloPay
    public Map<String, Object> createOrder(Map<String, Object> orderRequest) {
        Map<String, Object> result = new HashMap<>();

        try {
            Random rand = new Random();
            int randomId = rand.nextInt(1000000);

            // Lấy số tiền từ request
            Object amountObj = orderRequest.get("amount");
            if (amountObj == null) {
                result.put("error", "Amount is required");
                return result;
            }

            long amount = Long.parseLong(amountObj.toString());

            // Tạo thông tin đơn hàng
            Map<String, Object> order = new HashMap<>();
            order.put("app_id", ZalopayConfig.config.get("app_id"));
            String appTransId = getCurrentTimeString("yyMMdd") + "_" + randomId;
            order.put("app_trans_id", appTransId);
            order.put("app_time", System.currentTimeMillis());
            order.put("app_user", "user123"); // Thay thế bằng user thực tế
            order.put("amount", amount);
            order.put("description", "SN Mobile - Payment for the order #" + randomId);
            order.put("bank_code", ""); // Nếu không dùng ngân hàng, để trống
            order.put("item", "[{}]"); // Mã hóa thông tin sản phẩm
            order.put("embed_data", "{}"); // Dữ liệu nhúng thêm nếu cần
            order.put("callback_url", "https://your-ngrok-or-production-url/api/zalopay/callback");

            // Tạo chuỗi data để mã hóa MAC
            String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|"
                    + order.get("amount") + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|"
                    + order.get("item");

            String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);
            order.put("mac", mac);

            // Gửi yêu cầu đến API ZaloPay
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(ZalopayConfig.config.get("endpoint"));

                List<NameValuePair> params = new ArrayList<>();
                for (Map.Entry<String, Object> entry : order.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }

                post.setEntity(new UrlEncodedFormEntity(params));

                try (CloseableHttpResponse response = client.execute(post)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder resultJsonStr = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        resultJsonStr.append(line);
                    }

                    System.out.println("ZaloPay Response: " + resultJsonStr);

                    // Parse response JSON string to Map (dùng Jackson)
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> jsonResponse = objectMapper.readValue(resultJsonStr.toString(), Map.class);

                    // Kiểm tra phản hồi từ ZaloPay
                    if (jsonResponse.containsKey("order_url")) {
                        result.put("orderUrl", jsonResponse.get("order_url"));
                        result.put("zp_trans_token", jsonResponse.get("zp_trans_token"));
                    } else {
                        String errorMessage = String.valueOf(jsonResponse.getOrDefault("return_message", "Unknown error from ZaloPay"));
                        result.put("error", "ZaloPay API error: " + errorMessage);
                    }
                    
                    
                    return result;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "Failed to create order: " + e.getMessage());
            return result;
        }
    }

    // Lấy trạng thái đơn hàng
    public String getOrderStatus(String appTransId) {
        String data = ZalopayConfig.config.get("app_id") + "|" + appTransId + "|" + ZalopayConfig.config.get("key1");
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConfig.config.get("key1"), data);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(ZalopayConfig.config.get("orderstatus"));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("app_id", ZalopayConfig.config.get("app_id")));
            params.add(new BasicNameValuePair("app_trans_id", appTransId));
            params.add(new BasicNameValuePair("mac", mac));

            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder resultJsonStr = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    resultJsonStr.append(line);
                }

                return resultJsonStr.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to get order status: " + e.getMessage() + "\"}";
        }
    }
}
