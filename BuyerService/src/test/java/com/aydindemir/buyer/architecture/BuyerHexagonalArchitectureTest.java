package com.aydindemir.buyer.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.aydindemir.buyer",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class BuyerHexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnApplicationOrAdapters =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..application..",
                            "..adapter.."
                    );

    @ArchTest
    static final ArchRule domainMustRemainFrameworkIndependent =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "com.couchbase..",
                            "jakarta.."
                    );

    @ArchTest
    static final ArchRule applicationMustNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule inboundAdaptersMustNotDependOnOutboundAdapters =
            noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule restControllersMustDependOnInboundPorts =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .and().resideInAPackage("..adapter.in.rest..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application.port.in..");

    @ArchTest
    static final ArchRule couchbasePersistenceAdapterMustImplementOutboundPort =
            classes()
                    .that().haveSimpleName("CouchbaseBuyerPreferencesAdapter")
                    .should().implement(
                            "com.aydindemir.buyer.application.port.out.SaveBuyerPreferencesPort"
                    )
                    .andShould().implement(
                            "com.aydindemir.buyer.application.port.out.LoadBuyerPreferencesPort"
                    );

    @ArchTest
    static final ArchRule topLevelPackagesMustBeFreeOfCycles =
            slices()
                    .matching("com.aydindemir.buyer.(*)..")
                    .should().beFreeOfCycles();
}
