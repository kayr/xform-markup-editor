package org.openxdata.markup.exception

import groovy.transform.CompileStatic
import org.openxdata.markup.Form

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class DuplicateFormException extends Exception {
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
        return "Question[ " + String.valueOf(form1.getName()) + " ] and\n[ " + String.valueOf(form2.getName()) + " ] generate the same binding. Please try to make sure the question are not duplicates"
    }

    Form getForm1() {
        return form1
    }

    void setForm1(Form form1) {
        this.form1 = form1
    }

    Form getForm2() {
        return form2
    }

    void setForm2(Form form2) {
        this.form2 = form2
    }

    private Form form1
    private Form form2
}
