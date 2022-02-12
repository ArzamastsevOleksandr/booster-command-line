package uploadservice;

enum XlsxNoteColumn {

    CONTENT(0, "content"),
    TAGS(1, "tags");

    final int position;
    final String name;

    XlsxNoteColumn(int position, String name) {
        this.position = position;
        this.name = name;
    }

}
