package uploadservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum XlsxNoteColumn {

    CONTENT(0, "content"),
    TAGS(1, "tags"),
    LAST_SEEN_AT(2, "last seen at");

    final int position;
    final String name;

}
