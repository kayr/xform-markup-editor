package org.openxdata.markup.util

import groovy.transform.CompileStatic
import org.openxdata.markup.exception.InvalidAttributeException
import org.openxdata.markup.exception.ValidationException

/**
 * Created by user on 6/30/2017.
 */
@CompileStatic
class Assert {
    static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message)
        }
    }

    static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message)
        }
    }

    static void attribute(boolean expression, String message, int line) {
        if (!expression) {
            throw new InvalidAttributeException(message, line)
        }
    }

    static void isValid(boolean expression, String message, int line) {
        if (!expression) {
            throw new ValidationException(message, line)
        }
    }


}
