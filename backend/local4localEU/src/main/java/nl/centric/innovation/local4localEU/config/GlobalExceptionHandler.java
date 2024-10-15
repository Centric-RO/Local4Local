package nl.centric.innovation.local4localEU.config;

import io.jsonwebtoken.ExpiredJwtException;
import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;
import nl.centric.innovation.local4localEU.exception.RefreshTokenException;
import nl.centric.innovation.local4localEU.exception.CustomException.PasswordSameException;
import org.springframework.beans.factory.annotation.Value;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateAlreadyExistsException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.InvalidRoleException;
import nl.centric.innovation.local4localEU.exception.L4LEUException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@PropertySource({"classpath:errorcodes.properties"})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${error.code.header}")
    private String errorCodeHeader;

    @Value("${error.unexpected.role}")
    private String errorUnexpectedRole;

    @Value("${error.jwt.expired}")
    private String errorJwtExpired;

    @ExceptionHandler({
            DtoValidateException.class,
            DtoValidateNotFoundException.class,
            L4LEUException.class,
            NotFoundException.class
    })
    protected ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DtoValidateAlreadyExistsException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(DtoValidateAlreadyExistsException ex) {
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CaptchaException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ErrorResponse> handleCaptchaException(CaptchaException ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationLoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errorJwtExpired));
    }

    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(errorCodeHeader,
                "Illegal access exception").build();
    }

    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<ErrorResponse> handleInvalidRoleException(InvalidRoleException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header(errorCodeHeader, errorUnexpectedRole)
                .body(new ErrorResponse(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = {PasswordSameException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleSamePasswordException(
            PasswordSameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {RecoverException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<ErrorResponse> handleRecoveryException(RecoverException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {TalerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ErrorResponse> handleTalerException(TalerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }

    public record ErrorResponse(String message) {
    }
}