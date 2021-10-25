package com.booster;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Main {
    public static void main(String[] args) {
        // todo: UPDATE_VOCABULARY_ENTRY
        // todo: UPDATE_VOCABULARY
        // todo: add contexts to the vocabulary entry
        // todo: can exit the training session before it ends
        // todo: training session for v | lbl

        // todo: if I add the same ve, ask if I want to merge the result

        // todo: a strategy for which words can be a part of the training session.
        //  If the word has cac=N and lastDatePracticed=Today, exclude this word from the session

        // todo: help <command>
        // todo: list ve vid=<> | lblid=<>
        // todo: list v lblid=<>
        // todo: v has numberOfEntries field

        // todo: I can export all my vocabularies into the .pdf file

        // todo: later: statistics collector, which commands are used, how often
        // todo: distinguish upper-lower case?
        // todo: pretty print of l, lbl, v, ve, w
        // todo: I can mark the word as learned for it not to appear in the training sessions
        // todo: I can mark the word as hard for it to always appear in the training sessions
        // todo: I can search for the vocabulary entry by a substring
        // todo: I can move the ve from one v to the other
        // todo: I can copy the ve from one v to the other
        // todo: I can create a new ve without specifying the vid - it should appear for the DEFAULT vocabulary
        // todo: the DEFAULT vocabulary can not be renamed/deleted
        var applicationContext = new AnnotationConfigApplicationContext(Main.class);
        var learningSessionManager = applicationContext.getBean("learningSessionManager", LearningSessionManager.class);
        learningSessionManager.launch();
        // todo: learn: use CompositeKey abstraction in the maps if the key is str1+str2
        // todo: learn: avoid using null with the help of:
        //  null-safe api (2 methods, 1 accepts a null, 2 has fewer variables and allows you not to pass a null);
        //  custom data structures
//        ===============================================
// todo: standardize maven builds with maven wrapper
// todo: learn javac
// todo: java flame graphs
        // todo: inline types: stack gives more cache-friendliness vs heap

// todo: Chapter 33: crash JVM examples
    }


}
