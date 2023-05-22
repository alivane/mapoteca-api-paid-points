package co.mapoteca.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private final GeneralConfig generalConfig;

    public OpenApiConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mapoteca Paid Points service")
                        .description("""
                                <div id='section/Common-Status-Codes' data-section-id='section/Common-Status-Codes' class='sc-eCApnc'>
                                    <h1 class='chOOHy'><a class='sc-crzoAE DykGo' href='#section/Common-Status-Codes' aria-label='section/Common-Status-Codes'></a>Common Status Codes</h1>
                                    <table>
                                        <thead>
                                        <tr>
                                            <th>Status Code</th>
                                            <th>Description</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td>400 Bad Request</td>
                                            <td>Parámetros o formato inválido, campos faltantes</td>
                                        </tr>
                                        <tr>
                                            <td>401 Unauthorized</td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>403 Forbidden</td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>404 Not Found</td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td>500 Internal Server Error</td>
                                            <td></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                """)
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0"))
                        .contact(new Contact().name("keqing.dev")))
                .addServersItem(new Server().url(generalConfig.getServerUrl()))
                .components(new Components().addSecuritySchemes(
                        "JWT",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")
                ));
    }
}
