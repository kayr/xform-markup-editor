package org.openxdata.markup.exception

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
class ValidationException extends Exception {

    ValidationException() {
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

}
