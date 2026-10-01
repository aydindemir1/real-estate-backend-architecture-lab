package com.aydindemir.property.support;

import com.aydindemir.property.PropertyServiceApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;

@SpringBootTest(
        classes = PropertyServiceApplication.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:classpath:/property-test.properties",
                "eureka.client.enabled=false"
        }
)
public abstract class PropertyMongoContainerTestBase {

    protected static final String DATABASE = "property_service_test";

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0.30");

    static {
        MONGO.start();
    }

    @DynamicPropertySource
    static void configureMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", MONGO::getConnectionString);
        registry.add("spring.mongodb.database", () -> DATABASE);
    }
}
