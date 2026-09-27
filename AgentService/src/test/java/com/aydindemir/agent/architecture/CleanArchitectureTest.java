package com.aydindemir.agent.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.aydindemir.agent",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class CleanArchitectureTest {

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
    static final ArchRule presentationMustNotDependOnPersistenceInfrastructure =
            noClasses()
                    .that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule domainMustRemainFrameworkIndependent =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "org.hibernate.."
                    );
}
