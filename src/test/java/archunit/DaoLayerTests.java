package archunit;

import com.booster.dao.*;
import com.booster.service.*;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DaoLayerTests {

    JavaClasses javaClasses = new ClassFileImporter().importPackages("com.booster");

    static Stream<Arguments> daoClass2ServiceClassPairs() {
        return Stream.of(
                Arguments.of(NoteDao.class, NoteService.class),
                Arguments.of(LanguageDao.class, LanguageService.class),
                Arguments.of(SettingsDao.class, SettingsService.class),
                Arguments.of(TagDao.class, TagService.class),
                Arguments.of(WordDao.class, WordService.class),
                Arguments.of(VocabularyEntryDao.class, VocabularyEntryService.class)
        );
    }

    @ParameterizedTest(name = "{index} {0} is only used in {1}")
    @MethodSource("daoClass2ServiceClassPairs")
    void test(Class<?> daoClass, Class<?> serviceClass) {
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .belongToAnyOf(daoClass)
                .should()
                .onlyBeAccessed()
                .byClassesThat()
                .belongToAnyOf(serviceClass, daoClass);

        rule.check(javaClasses);
    }

}
