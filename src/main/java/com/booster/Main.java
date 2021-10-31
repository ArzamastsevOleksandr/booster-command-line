package com.booster;

import com.booster.parser.LexicalAnalyzer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Main {
    public static void main(String[] args) {
        // todo: I can add a new language to the system
        // todo: I can delete a language from the system
        // todo: I can create a tag
        // todo: I can tag the ve
        // todo: I can search for words by tags
        // todo: I can mark the ve as learned for it not to appear in the training sessions
        // todo: I can mark the ve as hard for it to always appear in the training sessions
        // todo: I can search for the ve by a substring
        // todo: add contexts to the ve
        // todo: can exit the training session before it ends
        // todo: a strategy for which words can be a part of the training session.
        //  If the word has cac=N and lastDatePracticed=Today, exclude this word from the session
        // todo: I can add notes
        // todo: I can see all notes
        // todo: I can delete a note
        // todo: I can tag the note
        // todo: when I have seen the note - update it to decrease its precedence for it not to occur in the list of notes again
        // todo: I can have a calc training session
        // todo: I have a level in calc
        // todo: Correct calc answers increase level and complexity of all subsequent calcs
        // todo: I can have a mul/div/sub/add/mixed training sessions in calc
//        var applicationContext = new AnnotationConfigApplicationContext(Main.class);
//        var learningSessionManager = applicationContext.getBean("learningSessionManager", LearningSessionManager.class);
//        learningSessionManager.launch();
        String s = " ave \\n=12\\s=s \\a=a \\d=d d d d d  ";
        System.out.println(s);
        System.out.println();
        new LexicalAnalyzer()
                .parse(s)
                .forEach(System.out::println);
        // todo: learn: use CompositeKey abstraction in the maps if the key is str1+str2
        // todo: learn: avoid using null with the help of:
        //  null-safe api (2 methods, 1 accepts a null, 2 has fewer variables and allows you not to pass a null);
        //  custom data structures
        // todo: organize packages by features, having private/default access modifiers for most methods
        // todo: standardize maven builds with maven wrapper
        // todo: learn javac
        // todo: java flame graphs
        // todo: inline types: stack gives more cache-friendliness vs heap
        // todo: Chapter 33: crash JVM examples, Chapter 68, 79
        // todo: custom command grammar and parser?
        // todo: fix: ave command: description of > 1 word can not be recognized
        // todo: UPDATE_VOCABULARY_ENTRY command
        // todo: UPDATE_SETTINGS command?
        // todo: distinguish upper-lower case?
        // todo: if I add the same ve, ask if I want to merge the result
        // todo: list_flags command
        // todo: HELP <command>
        // todo: pretty print of l, ve, w
        // todo: upgrade to latest Java
        // todo: value objects with no getters an setters, having public final fields
        // todo: check deps upgrades with mvn versions:display-dependency-updates (plugin)
        // todo: As soon as you see, or think, the word “and” in the description of a function, method, or class,
        //  you should hear alarm bells ringing inside your head.
        // todo: a task to add fractions (1/3 + 4/8 + 3/2)
        // todo: play with JShell
    }

}
