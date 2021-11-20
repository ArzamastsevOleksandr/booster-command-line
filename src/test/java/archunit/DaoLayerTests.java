package archunit;

import com.booster.dao.*;
import com.booster.service.*;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class DaoLayerTests {

    @Test
    void daoClassesShouldHaveAllDependenciesFinal() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("com.booster.dao")
                .should()
                .haveOnlyFinalFields();

        rule.check(javaClasses);
    }

    // todo: repeatable test
    @Test
    void noteDaoIsOnlyAccessedInNoteService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(NoteDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(NoteService.class, NoteDao.class);

        rule.check(javaClasses);
    }

    @Test
    void languageDaoIsOnlyAccessedInLanguageService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(LanguageDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(LanguageService.class, LanguageDao.class);

        rule.check(javaClasses);
    }

    @Test
    void settingsDaoIsOnlyAccessedInSettingsService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(SettingsDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(SettingsService.class, SettingsDao.class);

        rule.check(javaClasses);
    }

    @Test
    void tagDaoIsOnlyAccessedInTagService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(TagDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(TagService.class, TagDao.class);

        rule.check(javaClasses);
    }

    @Test
    void wordDaoIsOnlyAccessedInWordService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(WordDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(WordService.class, WordDao.class);

        rule.check(javaClasses);
    }

    @Test
    void vocabularyEntryDaoIsOnlyAccessedInVocabularyEntryService() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(VocabularyEntryDao.class)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(VocabularyEntryService.class, VocabularyEntryDao.class);

        rule.check(javaClasses);
    }

}
