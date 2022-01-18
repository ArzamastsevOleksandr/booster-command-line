package notesservice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
class HttpErrorResponse {

    private final ZonedDateTime timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    HttpErrorResponse() {
        this.timestamp = null;
        this.httpStatus = null;
        this.path = null;
        this.message = null;
    }

    HttpErrorResponse(HttpStatus httpStatus, String path, String message) {
        timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }

}
