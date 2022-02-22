package cliclient.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PropertyHolder {

    @Value("${session.vocabulary.size:5}")
    private int entriesPerVocabularyTrainingSession;

    @Value("${upload.filename:upload.xlsx}")
    private String uploadFilename;
    @Value("${download.filename:download.xlsx}")
    private String downloadFilename;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${pagination:5}")
    private int defaultPagination;

}
