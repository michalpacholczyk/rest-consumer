package pl.michal.pacholczyk.restconsumer.common.exception;

public class CustomRequestException extends RuntimeException {

    public CustomRequestException(String msg) {
        super("Request proccesing issue. Detailed message: " + msg);
    }

}
