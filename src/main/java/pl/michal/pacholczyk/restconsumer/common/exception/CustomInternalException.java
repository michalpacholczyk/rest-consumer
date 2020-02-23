package pl.michal.pacholczyk.restconsumer.common.exception;

public class CustomInternalException extends RuntimeException {

    public CustomInternalException() {
        super("Internal exception occurred. Please contact support!");
    }
}
