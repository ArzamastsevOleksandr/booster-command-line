package uploadservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum XlsxSettingsColumn {

    DEFAULT_LANGUAGE_NAME(0, "default language name"),
    ENTRIES_PER_VOCABULARY_TRAINING_SESSION(1, "entries per vocabulary training session"),
    LANGUAGES_PAGINATION(2, "languages pagination"),
    TAGS_PAGINATION(3, "tags pagination"),
    NOTES_PAGINATION(4, "notes pagination"),
    VOCABULARY_PAGINATION(5, "vocabulary pagination");

    final int position;
    final String name;

}
