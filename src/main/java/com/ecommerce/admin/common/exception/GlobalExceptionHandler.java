package com.ecommerce.admin.common.exception;

import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e) {
        return ResponseEntity.status(e.status())
                .body(ApiErrorResponse.of(e.title(), e.getMessage(), e.code(), e.property()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("Conflict", "The request conflicts with existing data",
                        "DATA_CONFLICT", null));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(OptimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("Concurrent modification",
                        "The resource was modified by someone else, reload it and try again",
                        "CONCURRENT_MODIFICATION", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("Internal server error", "An unexpected error occurred",
                        "INTERNAL_ERROR", null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError("Invalid field", error.getDefaultMessage(),
                        toErrorCode(error.getCode()), error.getField()));
        var globalErrors = e.getBindingResult().getGlobalErrors().stream()
                .filter(error -> !(error instanceof FieldError))
                .map(error -> new ApiError("Invalid request", error.getDefaultMessage(),
                        toErrorCode(error.getCode()), null));
        List<ApiError> errors = Stream.concat(fieldErrors, globalErrors).toList();
        return handleExceptionInternal(e, new ApiErrorResponse(errors), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String property = e instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : e.getPropertyName();
        var body = ApiErrorResponse.of("Invalid parameter", "Invalid value '" + e.getValue() + "'",
                "INVALID_PARAMETER", property);
        return handleExceptionInternal(e, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var body = ApiErrorResponse.of("Malformed request", "The request body is missing or isn't valid JSON",
                "MALFORMED_JSON", null);
        return handleExceptionInternal(e, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (!(body instanceof ApiErrorResponse)) {
            var httpStatus = HttpStatus.resolve(status.value());
            String title = httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
            String code = httpStatus != null ? httpStatus.name() : "ERROR";
            String detail = body instanceof ProblemDetail problem && problem.getDetail() != null
                    ? problem.getDetail()
                    : title;
            body = ApiErrorResponse.of(title, detail, code, null);
        }
        return super.handleExceptionInternal(e, body, headers, status, request);
    }

    private static String toErrorCode(String constraintName) {
        if (constraintName == null) {
            return "INVALID";
        }
        return constraintName.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
