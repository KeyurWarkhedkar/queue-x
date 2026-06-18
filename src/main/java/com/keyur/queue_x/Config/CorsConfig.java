package com.keyur.queue_x.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Wildcard subdomain covers any Netlify deploy URL (e.g. random-name-123.netlify.app)
                // without needing to hardcode the exact URL before it's known.
                // file:// is included so the page also works when opened locally for quick testing.
                .allowedOriginPatterns(
                        "https://*.netlify.app",
                        "http://localhost:*",
                        "null" // browsers send Origin: null for file:// pages in some cases
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}