/**
 * Copyright (C) 2021 Idan Roz the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rozidan.springboot.dockersecret;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;

import static org.springframework.core.io.support.ResourcePatternUtils.getResourcePatternResolver;
import static org.springframework.util.StreamUtils.copyToString;

@Slf4j
public class DockerSecretsPostProcessor implements EnvironmentPostProcessor {

    private static final String SECRETS_PATH_PROPERTY = "secrets.path";
    private static final String SECRETS_PATH_DEFAULT = "file:/run/secrets/*";

    private final Map<String, CheckedFunction<Resource, String>> extractionFunctions = new HashMap<>();

    public DockerSecretsPostProcessor() {
        extractionFunctions.put("spring.", this::extractValue);
        extractionFunctions.put("spring64.", this::extractBase64Value);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String secretPath = System.getProperty(SECRETS_PATH_PROPERTY);
        secretPath = Objects.nonNull(secretPath) ? secretPath : System.getenv(SECRETS_PATH_PROPERTY);
        if (Objects.isNull(secretPath)) {
            secretPath = SECRETS_PATH_DEFAULT;
        }

        try {
            loadSecrets(environment, application, secretPath);
        } catch (Exception ex) {
            log.error("error reading secrets: {}", ex.getMessage());
        }
    }

    private void loadSecrets(ConfigurableEnvironment environment, SpringApplication application, String secretsPath) throws IOException {
        Properties props = new Properties();

        for (Resource resource : getResourcePatternResolver(application.getResourceLoader()).getResources(secretsPath)) {
            if (resource.isFile() && resource.getFile().isFile()) {
                String filename = resource.getFilename();
                if (Objects.nonNull(filename)) {
                    String prefix = extractPrefix(filename);
                    if (extractionFunctions.containsKey(prefix)) {
                        props.setProperty(filename.replaceFirst(prefix, ""),
                                extractionFunctions.get(prefix).apply(resource));
                    }
                }
            }
        }

        environment.getPropertySources().addLast(new PropertiesPropertySource(generatePropertySourceName(secretsPath), props));
    }

    private String generatePropertySourceName(String secretsPath) {
        return String.format("Container secrets converted to properties via location '%s' with '%s'", secretsPath, getClass().getName());
    }

    private String extractPrefix(String secretName) {
        return secretName.contains(".") ? String.format("%s.", secretName.split("\\.")[0]) : "";
    }

    private String extractValue(Resource resource) throws IOException {
        return copyToString(resource.getInputStream(), Charset.defaultCharset());
    }

    private String extractBase64Value(Resource resource) throws IOException {
        return StringUtils.toEncodedString(
                Base64Utils.decode(StreamUtils.copyToByteArray(resource.getInputStream())),
                Charset.defaultCharset());
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

}

