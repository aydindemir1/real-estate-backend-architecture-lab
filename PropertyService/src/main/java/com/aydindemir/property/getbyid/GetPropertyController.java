package com.aydindemir.property.getbyid;

import com.aydindemir.property.shared.domain.model.PropertyId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class GetPropertyController {

    private final GetPropertyHandler handler;

    public GetPropertyController(GetPropertyHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<GetPropertyResponse> get(@PathVariable String propertyId) {
        var result = handler.handle(new GetPropertyQuery(PropertyId.from(propertyId)));
        return ResponseEntity.ok(GetPropertyResponse.from(result));
    }
}
