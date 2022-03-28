package cliclient.command.args;

import cliclient.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartVocabularyTrainingSessionCmdArgs implements CmdArgs {

    VocabularyTrainingSessionMode mode;
    Integer entriesPerVocabularyTrainingSession;

    @Override
    public Command getCommand() {
        return Command.START_VOCABULARY_TRAINING_SESSION;
    }

}
