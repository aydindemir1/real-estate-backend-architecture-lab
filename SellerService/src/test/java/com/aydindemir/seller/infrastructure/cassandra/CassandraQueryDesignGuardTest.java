package com.aydindemir.seller.infrastructure.cassandra;

import com.aydindemir.seller.domain.repository.ListingSubmissionRepository;
import com.aydindemir.seller.infrastructure.cassandra.repository.SpringDataListingSubmissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.cassandra.repository.Query;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CassandraQueryDesignGuardTest {

    @Test
    void repositoryQueriesMustNotUseAllowFiltering() {
        Arrays.stream(SpringDataListingSubmissionRepository.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(Query.class))
                .filter(annotation -> annotation != null)
                .map(Query::value)
                .forEach(cql -> assertThat(cql)
                        .as("CQL must not use ALLOW FILTERING")
                        .doesNotContainIgnoringCase("ALLOW FILTERING"));
    }

    @Test
    void listingQueriesMustUseExactSellerMonthPartition() {
        Method listMethod = method("findBySellerAndMonth");
        String listCql = listMethod.getAnnotation(Query.class).value();

        assertThat(listCql)
                .containsIgnoringCase("seller_id = :sellerId")
                .containsIgnoringCase("year_month = :yearMonth")
                .doesNotContainIgnoringCase("ALLOW FILTERING");

        Method exactMethod = method("findExact");
        String exactCql = exactMethod.getAnnotation(Query.class).value();

        assertThat(exactCql)
                .containsIgnoringCase("seller_id = :sellerId")
                .containsIgnoringCase("year_month = :yearMonth")
                .containsIgnoringCase("created_at = :createdAt")
                .containsIgnoringCase("submission_id = :submissionId")
                .doesNotContainIgnoringCase("ALLOW FILTERING");
    }

    @Test
    void domainRepositoryMustRequirePartitionCoordinatesForExactLookup() throws Exception {
        Method method = ListingSubmissionRepository.class.getMethod(
                "findBySellerAndMonthAndId",
                com.aydindemir.seller.domain.model.SellerId.class,
                YearMonth.class,
                Instant.class,
                com.aydindemir.seller.domain.model.ListingSubmissionId.class);

        assertThat(method).isNotNull();
        assertThat(Arrays.stream(ListingSubmissionRepository.class.getMethods())
                .map(Method::getName))
                .doesNotContain("findById", "findAll", "findBySellerId");
    }

    @Test
    void springDataRepositoryMustNotExposeUnsupportedAdHocQueries() {
        assertThat(Arrays.stream(SpringDataListingSubmissionRepository.class.getDeclaredMethods())
                .map(Method::getName))
                .containsExactlyInAnyOrder("findBySellerAndMonth", "findExact");
    }

    private Method method(String name) {
        return Arrays.stream(SpringDataListingSubmissionRepository.class.getDeclaredMethods())
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
