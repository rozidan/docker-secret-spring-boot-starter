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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


@SpringBootTest
class DockerSecretsPostProcessorFailedTest {

    @Autowired
    private ApplicationContext context;

    @BeforeAll
    static void init() {
        System.setProperty("secrets.path", "classpath:/notfound/*");
    }

    @Test
    void whenLoadApp_thenLoadSecretIsFailed() {
        Assertions.assertTrue(true);
    }

    @SpringBootApplication
    public static class App {

    }
}

