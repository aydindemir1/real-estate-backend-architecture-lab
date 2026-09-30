package com.aydindemir.seller.presentation.rest;

import com.aydindemir.seller.application.query.ListSellerSubmissionsQuery;
import com.aydindemir.seller.application.service.ListingSubmissionApplicationService;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.presentation.rest.mapper.ListingSubmissionRestMapper;
import com.aydindemir.seller.presentation.rest.request.CreateListingSubmissionRequest;
import com.aydindemir.seller.presentation.rest.request.SubmitListingRequest;
import com.aydindemir.seller.presentation.rest.response.ListingSubmissionPageResponse;
import com.aydindemir.seller.presentation.rest.response.ListingSubmissionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/sellers/{sellerId}/listing-submissions")
@Validated
public class ListingSubmissionController {

    private final ListingSubmissionApplicationService applicationService;
    private final ListingSubmissionRestMapper restMapper;

    public ListingSubmissionController(
            ListingSubmissionApplicationService applicationService,
            ListingSubmissionRestMapper restMapper) {
        this.applicationService = applicationService;
        this.restMapper = restMapper;
    }

    @PostMapping
    public ResponseEntity<ListingSubmissionResponse> createListingSubmission(
            @PathVariable UUID sellerId,
            @Valid @RequestBody CreateListingSubmissionRequest request) {
        ListingSubmissionResponse response = restMapper.toResponse(
                applicationService.createListingSubmission(
                        restMapper.toCreateCommand(SellerId.of(sellerId), request)));

        return ResponseEntity
                .created(URI.create(
                        "/sellers/" + sellerId
                                + "/listing-submissions/"
                                + response.submissionId()))
                .body(response);
    }

    @PostMapping("/{submissionId}/submit")
    public ResponseEntity<ListingSubmissionResponse> submitListing(
            @PathVariable UUID sellerId,
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmitListingRequest request) {
        if (!submissionId.equals(request.submissionId())) {
            throw new IllegalArgumentException("submissionId path and request body must match");
        }

        ListingSubmissionResponse response = restMapper.toResponse(
                applicationService.submitListing(
                        restMapper.toSubmitCommand(SellerId.of(sellerId), request)));

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ListingSubmissionPageResponse> listSellerSubmissions(
            @PathVariable UUID sellerId,
            @RequestParam
            @Pattern(regexp = "\\d{4}-\\d{2}")
            String yearMonth,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int pageSize,
            @RequestParam(required = false)
            String pageState) {
        ListingSubmissionPageResponse response = restMapper.toPageResponse(
                applicationService.listSellerSubmissions(
                        new ListSellerSubmissionsQuery(
                                SellerId.of(sellerId),
                                YearMonth.parse(yearMonth),
                                pageSize,
                                pageState)));

        return ResponseEntity.ok(response);
    }
}
