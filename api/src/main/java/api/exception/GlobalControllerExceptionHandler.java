package api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorResponse handleNotFound(HttpServletRequest req, NotFoundException ex) {
        return buildHttpErrorResponse(NOT_FOUND, req, ex);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(XlsxStructureUnsupportedException.class)
    public HttpErrorResponse handleXlsxStructureUnsupported(HttpServletRequest req, XlsxStructureUnsupportedException ex) {
        return buildHttpErrorResponse(NOT_ACCEPTABLE, req, ex);
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public HttpErrorResponse handleIllegalState(HttpServletRequest req, IllegalStateException ex) {
        return buildHttpErrorResponse(CONFLICT, req, ex);
    }

    private HttpErrorResponse buildHttpErrorResponse(HttpStatus status, HttpServletRequest req, RuntimeException ex) {
        String path = req.getServletPath();
        String message = ex.getMessage();

        log.error("Returning HTTP status: {} for path: {}, message: {}", status, path, message);
        return new HttpErrorResponse(status, path, message);
    }

}
