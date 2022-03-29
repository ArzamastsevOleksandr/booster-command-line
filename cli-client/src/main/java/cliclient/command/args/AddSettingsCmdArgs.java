package cliclient.command.args;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddSettingsCmdArgs implements CmdArgs {

    String defaultLanguageName;

    Integer entriesPerVocabularyTrainingSession;

    Integer vocabularyPagination;
    Integer notesPagination;
    Integer languagesPagination;
    Integer tagsPagination;
    // todo: print a message if defaults are used

}
