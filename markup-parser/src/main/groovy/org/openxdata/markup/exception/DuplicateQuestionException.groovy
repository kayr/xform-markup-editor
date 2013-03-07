package org.openxdata.markup.exception

import org.openxdata.markup.IQuestion

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
class DuplicateQuestionException extends Exception {

    IQuestion question1
    IQuestion question2

    DuplicateQuestionException() {
    }


    DuplicateQuestionException(String message) {
        super(message)
    }

    DuplicateQuestionException(String message, Throwable cause) {
        super(message, cause)
    }

    DuplicateQuestionException(Throwable cause) {
        super(cause)
    }

    @Override
    String getMessage() {
        return """Question[ $question1.text ] and
[ $question2.text ] generate the same binding. Please try to make sure the question are not duplicates"""
    }
}
