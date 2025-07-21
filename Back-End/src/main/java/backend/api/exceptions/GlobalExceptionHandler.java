package backend.api.exceptions;

import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import io.jsonwebtoken.security.SignatureException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private void logError(Exception e) {
        try (FileWriter fw = new FileWriter("error.txt", true)) { // append mode
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fw.write("[" + timestamp + "] " + e.toString() + "\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                fw.write("\tat " + ste + "\n");
            }
            fw.write("\n");
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    @ExceptionHandler({
            SignatureException.class,
            JwtException.class
    })
    public ResponseEntity<?> handleJwtExceptions(Exception e) {
        logError(e);
        String message = "Token de autentificare invalid";

        if (e instanceof SignatureException) {
            message = "Semnătura token-ului JWT nu este validă. Token-ul ar putea fi alterat.";
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "Unauthorized",
                        "message", message,
                        "code", 401
                ));
    }

    // ===== Excepții custom =====
    @ExceptionHandler(MyExeption.BadRequest.class)
    public ResponseEntity<?> handleBadRequest(MyExeption.BadRequest e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad request", "message", e.getMessage(), "code", 400));
    }

    @ExceptionHandler(MyExeption.Unauthorized.class)
    public ResponseEntity<?> handleUnauthorized(MyExeption.Unauthorized e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized", "message", e.getMessage(), "code", 401));
    }

    @ExceptionHandler(MyExeption.BadCredentials.class)
    public ResponseEntity<?> handleBadCredentials(MyExeption.BadCredentials e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Bad credentials", "message", e.getMessage(), "code", 401));
    }

    @ExceptionHandler(MyExeption.Forbidden.class)
    public ResponseEntity<?> handleForbidden(MyExeption.Forbidden e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Forbidden", "message", e.getMessage(), "code", 403));
    }

    @ExceptionHandler(MyExeption.NotFound.class)
    public ResponseEntity<?> handleNotFound(MyExeption.NotFound e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Not found", "message", e.getMessage(), "code", 404));
    }

    @ExceptionHandler(MyExeption.MethodNotAllowed.class)
    public ResponseEntity<?> handleMethodNotAllowedCustom(MyExeption.MethodNotAllowed e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Method Not Allowed", "message", e.getMessage(), "code", 405));
    }

    @ExceptionHandler(MyExeption.Conflict.class)
    public ResponseEntity<?> handleConflict(MyExeption.Conflict e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Conflict", "message", e.getMessage(), "code", 409));
    }

    @ExceptionHandler(MyExeption.InternalServerError.class)
    public ResponseEntity<?> handleInternalServerError(MyExeption.InternalServerError e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "message", e.getMessage(), "code", 500));
    }

    // ===== Excepții Spring - 400 Bad Request =====
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleSpringBadRequestExceptions(Exception e) {
        logError(e);
        String message = "Cerere incorectă";

        if (e instanceof HttpMessageNotReadableException) {
            message = "Format JSON invalid sau lipsă în cerere";
        } else if (e instanceof BindException) {
            message = "Validarea datelor a eșuat";
        } else if (e instanceof ConstraintViolationException) {
            message = "Încălcare a constrângerilor de validare";
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            message = "Tip de parametru incompatibil";
        } else if (e instanceof MissingServletRequestParameterException) {
            message = "Lipsă parametru obligatoriu";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", message, "code", 400));
    }

    // ===== Excepții Spring - 401 Unauthorized =====
    @ExceptionHandler({
            AuthenticationException.class,
            BadCredentialsException.class,
            DisabledException.class,
            LockedException.class
    })
    public ResponseEntity<?> handleSpringAuthenticationExceptions(Exception e) {
        logError(e);
        String message = "Autentificare eșuată";

        if (e instanceof BadCredentialsException) {
            message = "Credențiale invalide";
        } else if (e instanceof DisabledException) {
            message = "Contul este dezactivat";
        } else if (e instanceof LockedException) {
            message = "Contul este blocat";
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized", "message", message, "code", 401));
    }

    // ===== Excepții Spring - 403 Forbidden =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "Forbidden",
                        "message", "Nu aveți drepturile necesare pentru această acțiune.",
                        "code", 403
                ));
    }

    // ===== Excepții Spring - 404 Not Found =====
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleSpringNotFound(NoHandlerFoundException e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Not Found", "message", "Resursa solicitată nu a fost găsită", "code", 404));
    }

    // ===== Excepții Spring - 405 Method Not Allowed =====
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleSpringMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of(
                        "error", "Method Not Allowed",
                        "message", "Metoda HTTP nu este permisa pe acest endpoint.",
                        "code", 405
                ));
    }

    // ===== Excepții Spring - 406/415 Media Type =====
    @ExceptionHandler({
            HttpMediaTypeNotAcceptableException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<?> handleMediaTypeExceptions(Exception e) {
        logError(e);
        HttpStatus status = (e instanceof HttpMediaTypeNotAcceptableException)
                ? HttpStatus.NOT_ACCEPTABLE
                : HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        String message = (e instanceof HttpMediaTypeNotAcceptableException)
                ? "Tipul de media cerut nu este acceptat"
                : "Tipul de media furnizat nu este suportat";

        return ResponseEntity.status(status)
                .body(Map.of("error", status.getReasonPhrase(), "message", message, "code", status.value()));
    }

    // ===== Excepții Spring - 409 Conflict =====
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "Conflict",
                        "message", "Operațiunea nu poate fi finalizată din cauza unui conflict de date",
                        "code", 409
                ));
    }

    // ===== Excepții Spring - 413 Payload Too Large =====
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of(
                        "error", "Payload Too Large",
                        "message", "Dimensiunea fișierului încărcat depășește limita permisă",
                        "code", 413
                ));
    }

    // ===== Excepții Spring - 500 Internal Server Error =====
    @ExceptionHandler({
            DataAccessException.class,
            ExpressionException.class
    })
    public ResponseEntity<?> handleSpringServerErrors(Exception e) {
        logError(e);
        String message = "A apărut o eroare internă pe server";

        if (e instanceof DataAccessException) {
            message = "Eroare la accesarea bazei de date";
        } else if (e instanceof ExpressionException) {
            message = "Eroare la evaluarea expresiei";
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "message", message, "code", 500));
    }

    // ===== Excepție generică pentru orice altă excepție necaptată =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherErrors(Exception e) {
        logError(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "message", e.getMessage(), "code", 500));
    }
}