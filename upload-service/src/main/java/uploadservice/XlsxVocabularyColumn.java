package uploadservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum XlsxVocabularyColumn {

    WORD(0, "word"),
    DEFINITION(1, "definition"),
    SYNONYMS(2, "synonyms"),
    CORRECT_ANSWERS_COUNT(3, "correct answers count"),
    TAGS(4, "tags"),
    CONTEXTS(5, "contexts"),
    LAST_SEEN_AT(6, "last seen at");

    final int position;
    final String name;

}
