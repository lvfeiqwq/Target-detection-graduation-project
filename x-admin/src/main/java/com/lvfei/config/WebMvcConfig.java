package com.lvfei.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * 资源映射路径
         * addResourceHandler：访问映射路径
         * addResourceLocations：资源绝对路径
         */
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/Desktop/myproject/x-amdin/images/incident/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/detect/**")
                .addResourceLocations("file:///D:/Desktop/myproject/x-amdin/images/detect/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
