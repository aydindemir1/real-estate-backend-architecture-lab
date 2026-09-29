package com.aydindemir.buyer.adapter.out.persistence.couchbase;

import com.aydindemir.buyer.BuyerServiceApplication;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Optional;

@SpringBootTest(
        classes = BuyerServiceApplication.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:classpath:/buyer-test.properties",
                "eureka.client.enabled=false"
        }
)
public abstract class BuyerCouchbaseContainerTestBase {

    protected static final String BUCKET = "buyer";
    protected static final String SCOPE = "buyer_service";
    protected static final String COLLECTION = "preferences";

    static final CouchbaseContainer COUCHBASE = new CouchbaseContainer(
            DockerImageName.parse("couchbase:community-8.0.2")
                    .asCompatibleSubstituteFor("couchbase/server")
    )
            .withEnabledServices(
                    CouchbaseService.KV,
                    CouchbaseService.QUERY,
                    CouchbaseService.INDEX
            )
            .withBucket(new BucketDefinition(BUCKET));

    static {
        COUCHBASE.start();
        bootstrapScopeAndCollection();
    }

    @DynamicPropertySource
    static void configureCouchbase(DynamicPropertyRegistry registry) {
        registry.add("spring.couchbase.connection-string", COUCHBASE::getConnectionString);
        registry.add("spring.couchbase.username", COUCHBASE::getUsername);
        registry.add("spring.couchbase.password", COUCHBASE::getPassword);
        registry.add("spring.data.couchbase.bucket-name", () -> BUCKET);
        registry.add("spring.data.couchbase.scope-name", () -> SCOPE);
        registry.add("buyer.couchbase.collection-name", () -> COLLECTION);
    }

    private static void bootstrapScopeAndCollection() {
        try (Cluster cluster = Cluster.connect(
                COUCHBASE.getConnectionString(),
                COUCHBASE.getUsername(),
                COUCHBASE.getPassword()
        )) {
            var bucket = cluster.bucket(BUCKET);
            bucket.waitUntilReady(Duration.ofSeconds(30));

            CollectionManager collections = bucket.collections();

            Optional<ScopeSpec> scope = collections.getAllScopes()
                    .stream()
                    .filter(candidate -> SCOPE.equals(candidate.name()))
                    .findFirst();

            if (scope.isEmpty()) {
                collections.createScope(SCOPE);
                scope = collections.getAllScopes()
                        .stream()
                        .filter(candidate -> SCOPE.equals(candidate.name()))
                        .findFirst();
            }

            boolean collectionExists = scope
                    .map(existingScope -> existingScope.collections()
                            .stream()
                            .anyMatch(candidate -> COLLECTION.equals(candidate.name())))
                    .orElse(false);

            if (!collectionExists) {
                collections.createCollection(SCOPE, COLLECTION);
            }
        }
    }
}
