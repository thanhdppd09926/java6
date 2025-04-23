package ass.ass.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ZalopayConfig {

    public static final Map<String, String> config = new HashMap<String, String>() {
        {
            put("app_id", "553");
            put("key1", "9phuAOYhan4urywHTh0ndEXiV3pKHr5Q");
            put("key2", "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3");
            put("endpoint", "https://sb-openapi.zalopay.vn/v2/create");
            put("orderstatus", "https://sb-openapi.zalopay.vn/v2/query");

        }
    };
}