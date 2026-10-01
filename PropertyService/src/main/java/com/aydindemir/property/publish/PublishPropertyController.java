package com.aydindemir.property.publish;

import com.aydindemir.property.shared.domain.model.PropertyId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PublishPropertyController {

    private final PublishPropertyHandler handler;

    public PublishPropertyController(PublishPropertyHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/{propertyId}/publish")
    public ResponseEntity<PublishPropertyResponse> publish(@PathVariable String propertyId) {
        var result = handler.handle(new PublishPropertyCommand(PropertyId.from(propertyId)));
        return ResponseEntity.ok(PublishPropertyResponse.from(result));
    }
}
