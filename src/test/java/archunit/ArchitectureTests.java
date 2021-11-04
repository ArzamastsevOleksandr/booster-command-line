package archunit;

import com.booster.Main;
import com.booster.launcher.Launcher;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class ArchitectureTests {

    // fitness functions test how close the current design is to the desired architecture
    @Test
    void launcherIsAccessibleOnlyToItselfAndMainClass() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..launcher..")
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(Main.class, Launcher.class);

        rule.check(javaClasses);
    }

}
