package guru.qa.rococo.service;

import guru.qa.rococo.ex.AlreadyExistsException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.RequiredParamException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Value("${spring.application.name}")
  private String appName;

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorJson> handleNotFoundException(@Nonnull RuntimeException ex,
                                                           @Nonnull HttpServletRequest request) {
    LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
    return withStatus("Not found", HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ErrorJson> handleAlreadyExistsException(@Nonnull RuntimeException ex,
                                                           @Nonnull HttpServletRequest request) {
    LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
    return withStatus("Conflict Request", HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(RequiredParamException.class)
  public ResponseEntity<ErrorJson> handleRequiredParamException(@Nonnull RuntimeException ex,
                                                           @Nonnull HttpServletRequest request) {
    LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
    return withStatus("Bad request", HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @Override
  protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
                                                                         @Nonnull HttpHeaders headers,
                                                                         @Nonnull HttpStatusCode status,
                                                                         @Nonnull WebRequest request) {
    return ResponseEntity
            .status(status)
            .body(new ErrorJson(
                    appName + ": Entity validation error",
                    Objects.requireNonNull(HttpStatus.resolve(status.value())).getReasonPhrase(),
                    status.value(),
                    ex.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.joining(", ")),
                    ((ServletWebRequest) request).getRequest().getRequestURI()
            ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorJson> handleException(@Nonnull Exception ex,
                                                   @Nonnull HttpServletRequest request) {
    LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
    return withStatus("Internal error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
  }

  private @Nonnull ResponseEntity<ErrorJson> withStatus(@Nonnull String type,
                                                        @Nonnull HttpStatus status,
                                                        @Nonnull String message,
                                                        @Nonnull HttpServletRequest request) {
    return ResponseEntity
        .status(status)
        .body(new ErrorJson(
            appName + ": " + type,
            status.getReasonPhrase(),
            status.value(),
            message,
            request.getRequestURI()
        ));
  }
}
