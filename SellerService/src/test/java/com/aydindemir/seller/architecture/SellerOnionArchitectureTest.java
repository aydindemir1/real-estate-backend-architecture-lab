package com.aydindemir.seller.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "com.aydindemir.seller",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class SellerOnionArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnOuterLayers =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..application..",
                            "..infrastructure..",
                            "..presentation.."
                    );

    @ArchTest
    static final ArchRule applicationMustNotDependOnInfrastructureOrPresentation =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..presentation.."
                    );

    @ArchTest
    static final ArchRule infrastructureMustNotDependOnPresentation =
            noClasses()
                    .that().resideInAPackage("..infrastructure..")
                    .and().resideOutsideOfPackage("..infrastructure.configuration..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..presentation..");

    @ArchTest
    static final ArchRule presentationMustNotDependOnInfrastructure =
            noClasses()
                    .that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domainMustRemainFrameworkAndMessagingIndependent =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "org.springframework.data.cassandra..",
                            "com.datastax..",
                            "org.apache.kafka..",
                            "org.springframework.kafka..",
                            "org.springframework.amqp..",
                            "com.rabbitmq..",
                            "jakarta.."
                    );

    @ArchTest
    static final ArchRule applicationMustRemainFrameworkIndependent =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "com.datastax..",
                            "org.apache.kafka..",
                            "org.springframework.kafka..",
                            "org.springframework.amqp..",
                            "com.rabbitmq.."
                    );

    @ArchTest
    static final ArchRule CassandraAdaptersMustImplementDomainRepositoryContracts =
            classes()
                    .that().haveSimpleName("CassandraSellerRepositoryAdapter")
                    .should().implement(
                            "com.aydindemir.seller.domain.repository.SellerRepository"
                    )
                    .andShould().resideInAPackage("..infrastructure.cassandra.adapter..");

    @ArchTest
    static final ArchRule listingCassandraAdapterMustImplementDomainRepositoryContract =
            classes()
                    .that().haveSimpleName("CassandraListingSubmissionRepositoryAdapter")
                    .should().implement(
                            "com.aydindemir.seller.domain.repository.ListingSubmissionRepository"
                    )
                    .andShould().resideInAPackage("..infrastructure.cassandra.adapter..");

    @ArchTest
    static final ArchRule topLevelPackagesMustBeFreeOfCycles =
            slices()
                    .matching("com.aydindemir.seller.(*)..")
                    .should().beFreeOfCycles();
}
