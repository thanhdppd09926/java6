package ass.ass.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ZalopayConfig {

    public static final Map<String, String> config = new HashMap<String, String>() {
        {
            put("app_id", "2553");
            put("key1", "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL");
            put("key2", "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz");
            put("endpoint", "https://sb-openapi.zalopay.vn/v2/create");
            put("orderstatus", "https://sb-openapi.zalopay.vn/v2/query");

        }
    };
}