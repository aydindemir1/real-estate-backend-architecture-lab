package com.aydindemir.property.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "com.aydindemir.property",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class PropertyVerticalSliceArchitectureTest {

    @ArchTest
    static final ArchRule getByIdMustNotDependOnPublish =
            noClasses().that().resideInAPackage("..getbyid..")
                    .should().dependOnClassesThat().resideInAPackage("..publish..");

    @ArchTest
    static final ArchRule publishMustNotDependOnGetById =
            noClasses().that().resideInAPackage("..publish..")
                    .should().dependOnClassesThat().resideInAPackage("..getbyid..");

    @ArchTest
    static final ArchRule sharedMustNotDependOnFeatureSlices =
            noClasses().that().resideInAPackage("..shared..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..getbyid..", "..publish..");

    @ArchTest
    static final ArchRule domainMustRemainFrameworkIndependent =
            noClasses().that().resideInAPackage("..shared.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "com.mongodb..", "jakarta..");

    @ArchTest
    static final ArchRule topLevelSlicesMustBeFreeOfCycles =
            slices().matching("com.aydindemir.property.(*)..")
                    .should().beFreeOfCycles();
}
