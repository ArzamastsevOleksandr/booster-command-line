package uploadservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum XlsxTagColumn {

    NAME(0, "name");

    final int position;
    final String name;

}
