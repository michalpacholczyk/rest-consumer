package pl.michal.pacholczyk.restconsumer.restserver.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomInternalException;
import pl.michal.pacholczyk.restconsumer.common.exception.CustomRequestException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler({CustomRequestException.class, CustomInternalException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String employeeNotFoundHandler(RuntimeException ex) {
        return ex.getMessage();
    }
}
