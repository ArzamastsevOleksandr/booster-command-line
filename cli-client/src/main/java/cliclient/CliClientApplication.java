package cliclient;

import cliclient.launcher.Launcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class CliClientApplication {

    // todo: pom.xml optimization with dependency management
    public static void main(String[] args) {
        // todo: fix: dve => Cannot invoke "java.lang.Long.longValue()" because the return value of "cliclient.command.arguments.CommandWithArgs.getId()" is null
        // todo: fix: h j => Sequence must start with a command
        // todo: fix: n \pg=5 pg is ignored
        // todo: fix: ve \ss=a \pg=1 pg is ignored
        // todo: fix: ve \pg=1 pg is ignored
        // todo: dl => Cannot invoke "java.lang.Long.longValue()" because the return value of "cliclient.command.arguments.CommandWithArgs.getId()" is null
        // todo: >> svts Loaded 0 entries. Index 0 out of bounds for length 0
        // todo: a centralized validator for commands and flags
        // todo: fetch settings once and use the cached version
        // todo: amount of hints per session to settings
        // todo: help must describe optional & mandatory flags
        // todo: use @Value in 1 shared properties placeholder
        // todo: colored output everywhere
        // todo: help with pagination + help --all
        // todo: l, t: paginated output
        // todo: an \t= allow creating notes with tags
        // todo: if the first token is a recognized command, but the rest of the input is wrong - display an error message + the help on the command
        // todo: an NoteDto(id=336, content=null
        // todo: svts \c=10 to override training session size
        // todo: dl \lid=5 Cannot invoke "java.lang.Long.longValue()" because the return value of "cliclient.command.arguments.CommandWithArgs.getId()" is null
        // todo: at the end of the training session - save the details.
        //  I can later start a training session with words that I did not provide a correct answer for.
        // todo: if as used with no args - use system default values?
        // todo: fix: I can upload the same file many times
        // todo: money tracking
        // todo: favorite notes
        // todo: patch request based on the rfc standard
        // todo: settings service: it is not clear that only 1 instance of settings can exist for a user, make the flow standard,
        //  return all settings even if it only consists of 1 instance
        // todo: use color codes from adapter only
        // todo: implement support for flags with no values
        // todo: ave [500 ] during [POST] to [http://localhost:8082/vocabulary-entries/]
        // todo: add a new flag -ex (extended output). When writing to the output - present intel that is necessary. To present everything - use the extended flag.
        // todo: fix: add note, delete the same note - the counter of notes added must be correctly updated
        // todo: fix: if exception is thrown during the import process - the process stops
        // todo: update/delete tag
        // todo: have a pre-training session, where vocabulary entries are shown 1 by 1 with a delayed interval.
        //  I can then start the training session with these words.
        // todo: custom note impl (if the note is large - pretty print it
        // todo: feat: show all ves with descriptions/contexts only
        // todo: feat: connect ves (reluctance -> reluctant) and display all related ves when 1 is requested
        // todo: command to look for entries that have words in common (merge the entry into 1 single ve)
        // todo: ave \n=abound \s=be plentiful \d=exist in large numbers or amounts \c=Examples like this abound
        // todo: feat n \ss=<substr>
        // todo: enable adding ve/n with many tags
        // todo: uve \t \at \rt
        // todo: un \t \at \rt
        // todo: I can list the tags along with the count of items related to them
        // todo: fix: uve enable \ctx
        // todo: add logging to a file
        // todo: fix: ave \n=stuffy \s=airless;staid \d=(of a place) lacking fresh air or ventilation
        //Arguments must follow a pattern of flag -> separator -> value
        // todo: when importing a file - do not specify the extension
        // todo: HELP <command>
        // todo: pretty print of l, ve, w
        // todo: write 1-line open comments.
        // todo: I can search for words by tags
        // todo: I can mark the ve as learned for it not to appear in the training sessions
        // todo: I can mark the ve as hard for it to always appear in the training sessions
        // todo: I can have a calc training session
        // todo: I have a level in calc
        // todo: Correct calc answers increase level and complexity of all subsequent calcs
        // todo: I can have a mul/div/sub/add/mixed training sessions in calc
        // todo: use indexes in tables where frequent search is done
        // todo: add benchmarks for standard sql and sql with indexes
        // todo: I can manually increase/decrease calc session level
        // todo: concurrent import
        // todo: concurrent statistics collector
        // docker-compose logs -f (--tail=0 to see only new ones)
        // sudo docker rm -f $(sudo docker container ps -aq) & sudo docker-compose up -d
        ConfigurableApplicationContext context = SpringApplication.run(CliClientApplication.class, args);
        var launcher = context.getBean(Launcher.class);
        launcher.launch();
        context.registerShutdownHook();
        context.close();
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
