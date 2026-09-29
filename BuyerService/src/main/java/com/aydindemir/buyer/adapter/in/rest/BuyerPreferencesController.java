package com.aydindemir.buyer.adapter.in.rest;

import com.aydindemir.buyer.adapter.in.rest.mapper.BuyerPreferencesRestMapper;
import com.aydindemir.buyer.adapter.in.rest.request.AddSavedSearchRequest;
import com.aydindemir.buyer.adapter.in.rest.request.UpdateBuyerPreferencesRequest;
import com.aydindemir.buyer.adapter.in.rest.response.BuyerPreferencesResponse;
import com.aydindemir.buyer.application.port.in.AddSavedSearchUseCase;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesUseCase;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/buyers/{buyerId}")
public class BuyerPreferencesController {

    private final UpdateBuyerPreferencesUseCase updateBuyerPreferencesUseCase;
    private final GetBuyerPreferencesUseCase getBuyerPreferencesUseCase;
    private final AddSavedSearchUseCase addSavedSearchUseCase;
    private final BuyerPreferencesRestMapper mapper;

    public BuyerPreferencesController(
            UpdateBuyerPreferencesUseCase updateBuyerPreferencesUseCase,
            GetBuyerPreferencesUseCase getBuyerPreferencesUseCase,
            AddSavedSearchUseCase addSavedSearchUseCase,
            BuyerPreferencesRestMapper mapper
    ) {
        this.updateBuyerPreferencesUseCase = Objects.requireNonNull(
                updateBuyerPreferencesUseCase,
                "updateBuyerPreferencesUseCase must not be null"
        );
        this.getBuyerPreferencesUseCase = Objects.requireNonNull(
                getBuyerPreferencesUseCase,
                "getBuyerPreferencesUseCase must not be null"
        );
        this.addSavedSearchUseCase = Objects.requireNonNull(
                addSavedSearchUseCase,
                "addSavedSearchUseCase must not be null"
        );
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    @PutMapping("/preferences")
    public ResponseEntity<BuyerPreferencesResponse> updatePreferences(
            @PathVariable UUID buyerId,
            @Valid @RequestBody UpdateBuyerPreferencesRequest request
    ) {
        var result = updateBuyerPreferencesUseCase.update(
                mapper.toUpdateCommand(buyerId, request)
        );

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/preferences")
    public ResponseEntity<BuyerPreferencesResponse> getPreferences(
            @PathVariable UUID buyerId
    ) {
        var result = getBuyerPreferencesUseCase.get(
                mapper.toGetQuery(buyerId)
        );

        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/saved-searches")
    public ResponseEntity<BuyerPreferencesResponse> addSavedSearch(
            @PathVariable UUID buyerId,
            @Valid @RequestBody AddSavedSearchRequest request
    ) {
        var result = addSavedSearchUseCase.add(
                mapper.toAddSavedSearchCommand(buyerId, request)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(result));
    }
}
