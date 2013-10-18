package org.openxdata.markup.exception;

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationException extends Exception {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, int line) {
        this(message);
        this.line = line;
    }

    public ValidationException(String message, int line, Throwable e) {
        this(message);
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "[Line:" + String.valueOf(line) + ":] " + super.getMessage();
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    private int line;
}
