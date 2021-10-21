package com.booster;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// todo: standardize maven builds with maven wrapper
// todo: learn javac
// todo: java flame graphs
@Configuration
@ComponentScan
public class Main {
    public static void main(String[] args) {
        // todo: UPDATE_VOCABULARY_ENTRY
        // todo: UPDATE_VOCABULARY
        // todo: add contexts to the vocabulary entry
        // todo: parameterized training session: synonyms | antonyms | full
        // todo: can exit the training session before it ends
        // todo: training session for v | lbl

        // todo: if I add the same ve, ask if I want to merge the result

        // todo: a strategy for which words can be a part of the training session.
        //  If the word has cac=N and lastDatePracticed=Today, exclude this word from the session

        // todo: help <command>
        // todo: list ve vid=<> | lblid=<>
        // todo: list v lblid=<>
        // todo: all commands work with Args.class, remove @Deprecated collection<string>
        // todo: v has numberOfEntries field
        // todo: populate-sample-data.sql must have a variety of data to work with

        // todo: I can export all my vocabularies into the .xlsx file
        // todo: I can export all my vocabularies into the .pdf file

        // todo: I can import all my vocabularies from the .xlsx file

        // todo: later: statistics collector, which commands are used, how often
        // todo: distinguish upper-lower case?
        var applicationContext = new AnnotationConfigApplicationContext(Main.class);
        var learningSessionManager = applicationContext.getBean("learningSessionManager", LearningSessionManager.class);
        learningSessionManager.launch();
        // todo: learn: use CompositeKey abstraction in the maps if the key is str1+str2
        // todo: learn: avoid using null with the help of:
        //  null-safe api (2 methods, 1 accepts a null, 2 has fewer variables and allows you not to pass a null);
        //  custom data structures
    }

}
