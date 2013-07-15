package org.openxdata.markup.exception

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/9/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
class InvalidAttributeException extends ValidationException {

    InvalidAttributeException() {
    }

    InvalidAttributeException(String message) {
        super(message)
    }

    InvalidAttributeException(String message, Throwable cause) {
        super(message, cause)
    }

    InvalidAttributeException(Throwable cause) {
        super(cause)
    }

    InvalidAttributeException(String message, int line) {
        super(message, line)
    }
}
