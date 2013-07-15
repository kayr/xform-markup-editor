package org.openxdata.markup.exception

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
class ValidationException extends Exception {

    int line

    ValidationException() {
        super()
    }


    ValidationException(String message) {
        super(message)
    }

    ValidationException(String message, Throwable cause) {
        super(message, cause)
    }

    ValidationException(Throwable cause) {
        super(cause)
    }

    ValidationException(String message, int line) {
        this(message)
        this.line = line
    }

    ValidationException(String message, int line,Throwable e) {
        this(message)
        this.line = line
    }

    @Override
    String getMessage() {
        return "[Line:$line:] ${super.getMessage()}"
    }

}
