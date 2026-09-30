package com.aydindemir.seller.infrastructure.cassandra.mapper;

import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.infrastructure.cassandra.table.ListingSubmissionBySellerMonthTable;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Objects;

public final class ListingSubmissionCassandraMapper {

    public ListingSubmissionBySellerMonthTable toTable(ListingSubmission submission) {
        Objects.requireNonNull(submission, "submission must not be null");

        PropertyDraftData draft = submission.propertyDraftData();
        String yearMonth = YearMonth.from(submission.createdAt().atZone(ZoneOffset.UTC)).toString();

        return new ListingSubmissionBySellerMonthTable(
                submission.sellerId().value(),
                yearMonth,
                submission.createdAt(),
                submission.submissionId().value(),
                submission.status().name(),
                submission.updatedAt(),
                draft.title(),
                draft.description(),
                draft.propertyType(),
                draft.city(),
                draft.district(),
                draft.addressLine(),
                draft.priceAmount(),
                draft.currency(),
                draft.area(),
                draft.roomCount());
    }

    public ListingSubmission toDomain(ListingSubmissionBySellerMonthTable table) {
        Objects.requireNonNull(table, "table must not be null");

        PropertyDraftData draft = new PropertyDraftData(
                table.title(),
                table.description(),
                table.propertyType(),
                table.city(),
                table.district(),
                table.addressLine(),
                table.priceAmount(),
                table.currency(),
                table.area(),
                table.roomCount());

        return ListingSubmission.rehydrate(
                ListingSubmissionId.of(table.submissionId()),
                SellerId.of(table.sellerId()),
                draft,
                ListingSubmissionStatus.valueOf(table.status()),
                table.createdAt(),
                table.updatedAt());
    }
}
