package org.openxdata.markup.exception;

import org.openxdata.markup.IQuestion;

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateQuestionException extends Exception {
    public DuplicateQuestionException() {
    }

    public DuplicateQuestionException(String message) {
        super(message);
    }

    public DuplicateQuestionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateQuestionException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Question\n[ "
                + question1.getText(false) + " (line:"+  question1.getLine()+")] \nand\n ["
                + question2.getText(false)+ " (line:"+  question2.getLine()+ ")] \ngenerate the same binding.Please try to make sure the question are not duplicates ";
    }

    public IQuestion getQuestion1() {
        return question1;
    }

    public void setQuestion1(IQuestion question1) {
        this.question1 = question1;
    }

    public IQuestion getQuestion2() {
        return question2;
    }

    public void setQuestion2(IQuestion question2) {
        this.question2 = question2;
    }

    private IQuestion question1;
    private IQuestion question2;
}
