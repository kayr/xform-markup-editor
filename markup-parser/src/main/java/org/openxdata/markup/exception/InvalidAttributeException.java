package org.openxdata.markup.exception;

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/9/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidAttributeException extends ValidationException {
    public InvalidAttributeException() {
    }

    public InvalidAttributeException(String message) {
        super(message);
    }

    public InvalidAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAttributeException(Throwable cause) {
        super(cause);
    }

    public InvalidAttributeException(String message, int line) {
        super(message, line);
    }
}
