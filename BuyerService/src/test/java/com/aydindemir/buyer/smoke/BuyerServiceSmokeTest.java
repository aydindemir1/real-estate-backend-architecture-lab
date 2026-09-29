package com.aydindemir.buyer.smoke;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.BuyerCouchbaseContainerTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;

class BuyerServiceSmokeTest extends BuyerCouchbaseContainerTestBase {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Test
    void applicationContextShouldStartWithBuyerServiceIdentity() {
        assertThat(applicationContext).isNotNull();
        assertThat(environment.getProperty("spring.application.name"))
                .isEqualTo("buyer-service");
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
    }

    @Test
    void couchbaseConfigurationShouldUseDayNineKeyspace() {
        assertThat(environment.getProperty("spring.data.couchbase.bucket-name"))
                .isEqualTo(BUCKET);
        assertThat(environment.getProperty("spring.data.couchbase.scope-name"))
                .isEqualTo(SCOPE);
        assertThat(environment.getProperty("buyer.couchbase.collection-name"))
                .isEqualTo(COLLECTION);
    }
}
