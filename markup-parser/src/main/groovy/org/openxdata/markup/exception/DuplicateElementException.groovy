package org.openxdata.markup.exception

import groovy.transform.CompileStatic
import org.openxdata.markup.IFormElement

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
public class DuplicateElementException extends Exception {
    public DuplicateElementException() {
    }

    public DuplicateElementException(String message) {
        super(message);
    }

    public DuplicateElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateElementException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return """Element
[ ${question1.getText(false)} (line:${question1.getLine()})]
and
 [${question2.getText(false)} (line:${question2.getLine()})]
generate the same binding.Please try to make sure the question are not duplicates """;
    }

    public IFormElement getQuestion1() {
        return question1;
    }

    public void setQuestion1(IFormElement question1) {
        this.question1 = question1;
    }

    public IFormElement getQuestion2() {
        return question2;
    }

    public void setQuestion2(IFormElement question2) {
        this.question2 = question2;
    }

    private IFormElement question1;
    private IFormElement question2;
}
