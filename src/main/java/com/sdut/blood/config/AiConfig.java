package com.sdut.blood.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
@Slf4j
public class AiConfig {

    private static final String AI_SECRET_FILE = "config/application-ai-secret.yml";

    private static final String AI_SECRET_CLASSPATH = "application-ai-secret.yml";

    private String aiSecretSource = "not found";

    private final Properties aiSecretProperties = loadAiSecretProperties();

    @Value("${ai.openai.api-key:}")
    private String apiKey;

    @Value("${ai.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${ai.openai.model:gpt-3.5-turbo}")
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(120000);
        return new RestTemplate(requestFactory);
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

    @PostConstruct
    public void logAiConfigStatus() {
        String resolvedApiKey = getAiProperty("api-key", apiKey);
        String resolvedBaseUrl = getAiProperty("base-url", baseUrl);
        String resolvedModel = getAiProperty("model", model);
        log.info("AI config loaded from: {}, apiKeyConfigured={}, baseUrl={}, model={}",
                aiSecretSource,
                StringUtils.hasText(resolvedApiKey),
                StringUtils.hasText(resolvedBaseUrl) ? resolvedBaseUrl : "empty",
                StringUtils.hasText(resolvedModel) ? resolvedModel : "empty");
    }

    private String getAiProperty(String propertyName, String fallbackValue) {
        String propertyValue = aiSecretProperties.getProperty("ai.openai." + propertyName);
        return StringUtils.hasText(propertyValue) ? propertyValue.trim() : fallbackValue;
    }

    private Properties loadAiSecretProperties() {
        FileSystemResource fileResource = findAiSecretFile();
        Resource resource;
        if (fileResource.exists()) {
            resource = fileResource;
            aiSecretSource = fileResource.getPath();
        } else {
            resource = new ClassPathResource(AI_SECRET_CLASSPATH);
            aiSecretSource = resource.exists() ? "classpath:" + AI_SECRET_CLASSPATH : "not found";
        }
        if (!resource.exists()) {
            return new Properties();
        }

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        Properties properties = factory.getObject();
        return properties == null ? new Properties() : properties;
    }

    private FileSystemResource findAiSecretFile() {
        Path currentPath = Paths.get("").toAbsolutePath();
        while (currentPath != null) {
            Path candidate = currentPath.resolve(AI_SECRET_FILE);
            if (Files.exists(candidate)) {
                return new FileSystemResource(candidate);
            }
            currentPath = currentPath.getParent();
        }
        return new FileSystemResource(AI_SECRET_FILE);
    }
}
