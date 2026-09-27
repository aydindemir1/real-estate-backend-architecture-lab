package com.aydindemir.agent.smoke;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "eureka.client.enabled=false",
                "management.endpoints.web.exposure.include=health,info",
                "spring.flyway.enabled=true",
                "spring.jpa.hibernate.ddl-auto=validate"
        }
)
class AgentServiceSmokeTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("agent_service_smoke_test")
            .withUsername("agent_test")
            .withPassword("agent_test");

    @DynamicPropertySource
    static void configureMySql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Test
    void applicationContextShouldStartWithAgentServiceIdentity() {
        assertThat(applicationContext).isNotNull();
        assertThat(environment.getProperty("spring.application.name"))
                .isEqualTo("agent-service");
    }

    @Test
    void configClientAndEurekaClientShouldRemainOnClasspath() {
        ClassLoader classLoader = applicationContext.getClassLoader();

        assertThat(ClassUtils.isPresent(
                "org.springframework.cloud.config.client.ConfigClientProperties",
                classLoader
        )).isTrue();

        assertThat(ClassUtils.isPresent(
                "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
                classLoader
        )).isTrue();

        assertThat(environment.getProperty("spring.cloud.config.uri"))
                .isEqualTo("http://localhost:8889");
    }

    @Test
    void actuatorHealthShouldReportUp() {
        assertThat(healthEndpoint.health().getStatus()).isEqualTo(Status.UP);
    }
}
