package uploadservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum XlsxSettingsColumn {

    DEFAULT_LANGUAGE_NAME(0, "default language name"),
    ENTRIES_PER_VOCABULARY_TRAINING_SESSION(1, "entries per vocabulary training session");

    final int position;
    final String name;

}
