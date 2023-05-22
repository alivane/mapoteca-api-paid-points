package co.mapoteca.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("stripe")
@Configuration
@Getter
@Setter
public class StripeConfig {
    private String secretKey;
    private String webhookSecret;
    private String clientUrl;
    private String successUrl;
    private String cancelUrl;
    private String priceId;
}
