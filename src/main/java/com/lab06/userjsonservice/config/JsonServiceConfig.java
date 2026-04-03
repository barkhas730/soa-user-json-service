package com.lab06.userjsonservice.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.ws.client.WebServiceIOException;

@Configuration
public class JsonServiceConfig {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.lab06.userjsonservice.soap");
        return marshaller;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @ControllerAdvice
    static class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        @ResponseBody
        public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
            return Map.of("message", ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ResponseBody
        public Map<String, String> handleValidationError(MethodArgumentNotValidException ex) {
            return Map.of("message", "Shaardlagatai talbaruudiig zuv boglono uu.");
        }

        @ExceptionHandler(WebServiceIOException.class)
        @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
        @ResponseBody
        public Map<String, String> handleSoapConnectionError(WebServiceIOException ex) {
            return Map.of("message", "Auth service tur holbogdohgui baina.");
        }
    }
}
