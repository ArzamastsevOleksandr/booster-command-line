package archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class TestExample {

    // fitness functions test how close the current design is to the desired architecture
    @Test
    void test() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        // deliberately fail
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..handler..")
                .should().onlyBeAccessed()
                .byAnyPackage("..input..");

        rule.check(javaClasses);
    }

}
