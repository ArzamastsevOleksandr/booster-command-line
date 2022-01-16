package archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static archunit.ArchUnitTestConstants.*;

class SystemOutTests {

    static String ADAPTER_PACKAGE = CLI_CLIENT + PACKAGE_SEPARATOR + ADAPTER;

    JavaClasses javaClasses = new ClassFileImporter().importPackages(CLI_CLIENT);

    @BeforeAll
    static void beforeAll() {
        System.out.printf("Adapter package: %s\n", ADAPTER_PACKAGE);
    }

    @Test
    void outputIsPrintedOnlyByAdapter() {
        ArchRule rule = ArchRuleDefinition
                .noClasses()
                .that()
                .resideOutsideOfPackage(ADAPTER_PACKAGE)
                .should()
                .accessClassesThat()
                .belongToAnyOf(System.out.getClass());

        rule.check(javaClasses);
    }

}
