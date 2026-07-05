package com.sdut.blood.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class AiConfig {

    private static final String AI_SECRET_FILE = "config/application-ai-secret.yml";

    private static final String AI_SECRET_CLASSPATH = "application-ai-secret.yml";

    private final Properties aiSecretProperties = loadAiSecretProperties();

    @Value("${ai.openai.api-key:}")
    private String apiKey;

    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public String aiApiKey() {
        return getAiProperty("api-key", apiKey);
    }

    @Bean
    public String aiBaseUrl() {
        return getAiProperty("base-url", baseUrl);
    }

    @Bean
    public String aiModel() {
        return getAiProperty("model", model);
    }

    private String getAiProperty(String propertyName, String fallbackValue) {
        String propertyValue = aiSecretProperties.getProperty("ai.openai." + propertyName);
        return StringUtils.hasText(propertyValue) ? propertyValue : fallbackValue;
    }

    private Properties loadAiSecretProperties() {
        FileSystemResource fileResource = new FileSystemResource(AI_SECRET_FILE);
        Resource resource = fileResource.exists() ? fileResource : new ClassPathResource(AI_SECRET_CLASSPATH);
        if (!resource.exists()) {
            return new Properties();
        }

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        Properties properties = factory.getObject();
        return properties == null ? new Properties() : properties;
    }
}
