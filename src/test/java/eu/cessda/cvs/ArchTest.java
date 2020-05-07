package eu.cessda.cvs;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("eu.cessda.cvs");

        noClasses()
            .that()
                .resideInAnyPackage("eu.cessda.cvs.service..")
            .or()
                .resideInAnyPackage("eu.cessda.cvs.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..eu.cessda.cvs.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
