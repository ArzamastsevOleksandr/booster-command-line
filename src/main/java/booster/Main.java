package booster;

import booster.launcher.Launcher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Main {
    public static void main(String[] args) {
        // todo: custom note impl (if the note is large - pretty print it
        // todo: test dao layer (containers?)
        // todo: do we need to make read operations transactional?
        // todo: try using @Transactional (enable it)
        // todo: feat: show all ves with descriptions/contexts only
        // todo: feat: connect ves (reluctance -> reluctant) and display all related ves when 1 is requested
        // todo: command to look for entries that have words in common (merge the entry into 1 single ve)
        // todo: ave \n=abound \s=be plentiful \d=exist in large numbers or amounts \c=Examples like this abound
        // todo: n/ve \p=no
        // todo: feat n \ss=<substr>
        // todo: enable adding ve/n with many tags
        // todo: uve \t \at \rt
        // todo: un \t \at \rt
        // todo: I can list the tags along with the count of items related to them
        // todo: if null is returned when querying for count - log the error and return 0
        // todo: fix: uve enable \ctx
        // todo: add logging to a file
        // todo: fix: ave \n=stuffy \s=airless;staid \d=(of a place) lacking fresh air or ventilation
        //Arguments must follow a pattern of flag -> separator -> value
        // todo: when importing a file - do not specify the extension
        // todo: in the end of the training session print all words that had mistakes
        // todo: validator validates. command handler works with non-optional data and expects a happy path?
        // todo: list_flags command
        // todo: HELP <command>
        // todo: pretty print of l, ve, w
        // todo: write 1-line open comments.
        // todo: use settings in the preprocessor
        // todo: I can search for words by tags
        // todo: I can mark the ve as learned for it not to appear in the training sessions
        // todo: I can mark the ve as hard for it to always appear in the training sessions
        // todo: when I have seen the note - update it to decrease its precedence for it not to occur in the list of notes again
        // todo: I can have a calc training session
        // todo: I have a level in calc
        // todo: Correct calc answers increase level and complexity of all subsequent calcs
        // todo: I can have a mul/div/sub/add/mixed training sessions in calc
        // todo: use over(), rank() and partition by when searching for which ves to output
        // todo: use indexes in tables where frequent search is done
        // todo: add benchmarks for standard sql and sql with indexes
        // todo: I can manually increase/decrease calc session level
        // todo: concurrent import
        // todo: concurrent statistics collector
        // docker-compose logs -f (--tail=0 to see only new ones)
        // sudo docker rm -f $(sudo docker container ps -aq) & sudo docker-compose up -d
        var applicationContext = new AnnotationConfigApplicationContext(Main.class);
        var launcher = applicationContext.getBean("launcher", Launcher.class);
        launcher.launch();
        // todo: window functions (row_number() etc)
        // todo: learn: use CompositeKey abstraction in the maps if the key is str1+str2
        // todo: learn: avoid using null with the help of:
        //  null-safe api (2 methods, 1 accepts a null, 2 has fewer variables and allows you not to pass a null);
        //  custom data structures
        // todo: organize packages by features, having private/default access modifiers for most methods
        // todo: standardize maven builds with maven wrapper
        // todo: learn javac
        // todo: java flame graphs
        // todo: inline types: stack gives more cache-friendliness vs heap
        // todo: Chapter 33: crash JVM examples, Chapter 68, 79, 82, 84, 87, 95, 97
        // todo: custom command grammar and parser?
        // todo: UPDATE_SETTINGS command?
        // todo: distinguish upper-lower case?
        // todo: if I add the same ve, ask if I want to merge the result
        // todo: upgrade to latest Java
        // todo: value objects with no getters an setters, having public final fields
        // todo: check deps upgrades with mvn versions:display-dependency-updates (plugin)
        // todo: As soon as you see, or think, the word “and” in the description of a function, method, or class,
        //  you should hear alarm bells ringing inside your head.
        // todo: a task to add fractions (1/3 + 4/8 + 3/2)
        // todo: play with JShell
        // todo: implement custom annotations (Like, CommandHandler,
        //  to avoid creating redundant comments + to enable fitness function tests (architecture conformity).
    }

}
