package org.openxdata.markup.exception

import org.openxdata.markup.Form

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
class DuplicateFormException extends Exception {

    Form form1
    Form form2

    DuplicateFormException() {
    }


    DuplicateFormException(String message) {
        super(message)
    }

    DuplicateFormException(String message, Throwable cause) {
        super(message, cause)
    }

    DuplicateFormException(Throwable cause) {
        super(cause)
    }

    @Override
    String getMessage() {
        return """Question[ $question1.text ] and
[ $question2.text ] generate the same binding. Please try to make sure the question are not duplicates"""
    }
}
