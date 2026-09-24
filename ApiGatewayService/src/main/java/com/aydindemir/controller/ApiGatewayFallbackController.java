package com.aydindemir.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class ApiGatewayFallbackController {

    @RequestMapping("/auth")
    public ResponseEntity<String> authFallback() {
        return ResponseEntity.ok("AuthService geçici olarak hizmet veremiyor.");
    }

    @RequestMapping("/user")
    public ResponseEntity<String> userProfileFallback() {
        return ResponseEntity.ok("UserProfileService geçici olarak hizmet veremiyor.");
    }

    @RequestMapping("/agent")
    public ResponseEntity<String> agentFallback() {
        return ResponseEntity.ok("AgentService geçici olarak hizmet veremiyor.");
    }

    @RequestMapping("/buyer")
    public ResponseEntity<String> buyerFallback() {
        return ResponseEntity.ok("BuyerService geçici olarak hizmet veremiyor.");
    }

    @RequestMapping("/property")
    public ResponseEntity<String> propertyFallback() {
        return ResponseEntity.ok("PropertyService geçici olarak hizmet veremiyor.");
    }

    @RequestMapping("/seller")
    public ResponseEntity<String> sellerFallback() {
        return ResponseEntity.ok("SellerService geçici olarak hizmet veremiyor.");
    }
}
