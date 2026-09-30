package com.aydindemir.seller.infrastructure.cassandra;

import com.aydindemir.seller.SellerServiceApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        classes = SellerServiceApplication.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:configserver:",
                "eureka.client.enabled=false",
                "spring.cassandra.schema-action=none"
        }
)
public abstract class CassandraIntegrationTestSupport {

    protected static final String KEYSPACE = "seller_service";

    protected static final CassandraContainer CASSANDRA =
            new CassandraContainer(DockerImageName.parse("cassandra:5.0.9"))
                    .withInitScript("cassandra/schema/V1__seller_tables.cql");

    static {
        CASSANDRA.start();
    }

    @DynamicPropertySource
    static void configureCassandra(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cassandra.contact-points",
                () -> CASSANDRA.getContactPoint().getHostString());
        registry.add(
                "spring.cassandra.port",
                () -> CASSANDRA.getContactPoint().getPort());
        registry.add(
                "spring.cassandra.local-datacenter",
                CASSANDRA::getLocalDatacenter);
        registry.add(
                "spring.cassandra.keyspace-name",
                () -> KEYSPACE);
    }
}
