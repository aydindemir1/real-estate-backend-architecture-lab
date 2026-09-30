package com.aydindemir.seller.infrastructure.cassandra.adapter;

import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.repository.ListingSubmissionPage;
import com.aydindemir.seller.domain.repository.ListingSubmissionPageRequest;
import com.aydindemir.seller.domain.repository.ListingSubmissionRepository;
import com.aydindemir.seller.infrastructure.cassandra.mapper.ListingSubmissionCassandraMapper;
import com.aydindemir.seller.infrastructure.cassandra.repository.SpringDataListingSubmissionRepository;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CassandraListingSubmissionRepositoryAdapter implements ListingSubmissionRepository {

    private final SpringDataListingSubmissionRepository repository;
    private final ListingSubmissionCassandraMapper mapper;

    public CassandraListingSubmissionRepositoryAdapter(SpringDataListingSubmissionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.mapper = new ListingSubmissionCassandraMapper();
    }

    @Override
    public ListingSubmission save(ListingSubmission submission) {
        Objects.requireNonNull(submission, "submission must not be null");
        return mapper.toDomain(repository.save(mapper.toTable(submission)));
    }

    @Override
    public Optional<ListingSubmission> findBySellerAndMonthAndId(
            SellerId sellerId,
            YearMonth yearMonth,
            Instant createdAt,
            ListingSubmissionId submissionId) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(yearMonth, "yearMonth must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(submissionId, "submissionId must not be null");

        return repository.findExact(
                        sellerId.value(),
                        yearMonth.toString(),
                        createdAt,
                        submissionId.value())
                .map(mapper::toDomain);
    }

    @Override
    public ListingSubmissionPage listBySellerAndMonth(
            SellerId sellerId,
            YearMonth yearMonth,
            ListingSubmissionPageRequest pageRequest) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(yearMonth, "yearMonth must not be null");
        Objects.requireNonNull(pageRequest, "pageRequest must not be null");

        Pageable pageable = toPageable(pageRequest);
        Slice<com.aydindemir.seller.infrastructure.cassandra.table.ListingSubmissionBySellerMonthTable> slice =
                repository.findBySellerAndMonth(sellerId.value(), yearMonth.toString(), pageable);

        List<ListingSubmission> items = slice.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new ListingSubmissionPage(items, extractNextPageState(slice));
    }

    private Pageable toPageable(ListingSubmissionPageRequest pageRequest) {
        CassandraPageRequest firstPage = CassandraPageRequest.first(pageRequest.pageSize());

        if (pageRequest.pageState() == null) {
            return firstPage;
        }

        byte[] decoded = Base64.getUrlDecoder().decode(pageRequest.pageState());
        return CassandraPageRequest.of(firstPage, ByteBuffer.wrap(decoded));
    }

    private String extractNextPageState(Slice<?> slice) {
        if (!slice.hasNext()) {
            return null;
        }

        Pageable nextPageable = slice.nextPageable();
        if (!(nextPageable instanceof CassandraPageRequest cassandraPageRequest)) {
            throw new IllegalStateException("Expected CassandraPageRequest for Cassandra paging");
        }

        ByteBuffer pagingState = cassandraPageRequest.getPagingState();
        if (pagingState == null) {
            return null;
        }

        ByteBuffer readOnly = pagingState.asReadOnlyBuffer();
        byte[] bytes = new byte[readOnly.remaining()];
        readOnly.get(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
