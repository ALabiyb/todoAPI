package com.abdulmunim.apik8s.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Management API",
                version = "1.0.0",
                description = "A comprehensive REST API for managing tasks with priority levels",
                contact = @Contact(
                        name = "AbdulMunim",
                        email = "hackermunim@gmail.com",
                        url = "https://github.com/yourusername"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://your-prod-server.com", description = "Production Server")
        }
)
public class OpenApiConfig {
}