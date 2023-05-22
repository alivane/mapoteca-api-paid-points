package co.mapoteca.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("directopago")
@Component
@Getter
@Setter
public class DirectoPagoConfig {
    private String apiKey;
    private String secretKey;

    private String baseUrl;


    public String getBearer(){
        return this.apiKey +":"+this.secretKey;
    }
}
