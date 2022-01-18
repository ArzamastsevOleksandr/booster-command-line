package api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorResponse handleNoteByIdNotFound(HttpServletRequest req, NotFoundException ex) {
        return buildHttpErrorResponse(NOT_FOUND, req, ex);
    }

    private HttpErrorResponse buildHttpErrorResponse(HttpStatus status, HttpServletRequest req, NotFoundException ex) {
        String path = req.getServletPath();
        String message = ex.getMessage();

        log.error("Returning HTTP status: {} for path: {}, message: {}", status, path, message);
        return new HttpErrorResponse(status, path, message);
    }

}
