package com.booster;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Main {
    public static void main(String[] args) {
        // todo: I can mark the word as learned for it not to appear in the training sessions
        // todo: I can mark the word as hard for it to always appear in the training sessions
        // todo: I can search for the vocabulary entry by a substring
        // todo: I can move the ve from one v to the other
        // todo: I can copy the ve from one v to the other
        // todo: add contexts to the vocabulary entry
        // todo: can exit the training session before it ends
        // todo: training session for v | lbl
        // todo: a strategy for which words can be a part of the training session.
        //  If the word has cac=N and lastDatePracticed=Today, exclude this word from the session
        // todo: list ve vid=<> | lblid=<>
        // todo: list v lblid=<>
        // todo: v has numberOfEntries field
        var applicationContext = new AnnotationConfigApplicationContext(Main.class);
        var learningSessionManager = applicationContext.getBean("learningSessionManager", LearningSessionManager.class);
        learningSessionManager.launch();
        // todo: learn: use CompositeKey abstraction in the maps if the key is str1+str2
        // todo: learn: avoid using null with the help of:
        //  null-safe api (2 methods, 1 accepts a null, 2 has fewer variables and allows you not to pass a null);
        //  custom data structures
        // todo: organize packages by features, having private/default access modifiers for most methods
        // todo: standardize maven builds with maven wrapper
        // todo: learn javac
        // todo: java flame graphs
        // todo: inline types: stack gives more cache-friendliness vs heap
        // todo: Chapter 33: crash JVM examples
        // todo: custom command grammar and parser?
        // todo: fix: ave command: description of > 1 word can not be recognized
        // todo: UPDATE_VOCABULARY_ENTRY command
        // todo: UPDATE_VOCABULARY command
        // todo: UPDATE_SETTINGS command?
        // todo: distinguish upper-lower case?
        // todo: if I add the same ve, ask if I want to merge the result
        // todo: list_flags command
        // todo: HELP <command>
        // todo: pretty print of l, lbl, v, ve, w
        // todo: upgrade to latest Java
    }

}
