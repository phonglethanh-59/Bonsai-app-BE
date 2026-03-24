package com.vti.bevtilib.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /uploads/** tới thư mục vật lý `uploads/` trong thư mục gốc của project
        exposeDirectory("uploads", registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Cấu hình resource handler
        // URL pattern: /uploads/**
        // File location: file:/path/to/your/project/uploads/
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}