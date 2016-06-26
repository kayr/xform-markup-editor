package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
trait IQuestion implements IFormElement {

    String comment
    String type
    boolean required
    boolean readOnly
    String calculation
    def value


    void setText(String text) {
        if (text.startsWith('*')) {
            text = text[1..text.length() - 1]
            required = true
        }
        setName text
        def tempBind = Util.getBindName(text)
        if (!binding)
            binding = tempBind
        if (!type)
            type = Util.getType(tempBind)
    }

    String getText(boolean number) {
        if (number)
            return getNumberedText()
        return text

    }


    String getComment() {
        return comment
    }


    String toString() {
        return "$questionIdx. $text"
    }

}
