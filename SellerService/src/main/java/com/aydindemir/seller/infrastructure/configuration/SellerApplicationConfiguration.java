package com.aydindemir.seller.infrastructure.configuration;

import com.aydindemir.seller.application.service.ListingSubmissionApplicationService;
import com.aydindemir.seller.application.service.SellerApplicationService;
import com.aydindemir.seller.domain.repository.ListingSubmissionRepository;
import com.aydindemir.seller.domain.repository.SellerRepository;
import com.aydindemir.seller.presentation.rest.mapper.ListingSubmissionRestMapper;
import com.aydindemir.seller.presentation.rest.mapper.SellerRestMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SellerApplicationConfiguration {

    @Bean
    SellerApplicationService sellerApplicationService(SellerRepository sellerRepository) {
        return new SellerApplicationService(sellerRepository);
    }

    @Bean
    ListingSubmissionApplicationService listingSubmissionApplicationService(
            SellerRepository sellerRepository,
            ListingSubmissionRepository listingSubmissionRepository) {
        return new ListingSubmissionApplicationService(
                sellerRepository,
                listingSubmissionRepository);
    }

    @Bean
    SellerRestMapper sellerRestMapper() {
        return new SellerRestMapper();
    }

    @Bean
    ListingSubmissionRestMapper listingSubmissionRestMapper() {
        return new ListingSubmissionRestMapper();
    }
}
