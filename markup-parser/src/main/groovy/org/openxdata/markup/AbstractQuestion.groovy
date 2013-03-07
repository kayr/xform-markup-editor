package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractQuestion implements IQuestion {

    HasQuestions hasQuestions
    String question
    String comment
    boolean readOnly
    String type
    boolean required
    boolean visible = true
    String binding
    String skipLogic
    String skipAction
    String validationLogic
    String message
    String calculation


    AbstractQuestion() {

    }

    AbstractQuestion(String question) {
        setText(question)
    }

    @Override
    String getText() {
        return question
    }

    @Override
    void setText(String text) {
        if (!binding)
            binding = Util.getBindName(text)
        if (!type)
            type = Util.getType(binding)
        this.question = text
    }

    @Override
    String getBinding() {
        return binding
    }

    @Override
    String getFullBinding() {
        return "$hasQuestions.fullBinding/$binding"
    }

    @Override
    String getComment() {
        return comment
    }

    @Override
    void setParent(HasQuestions hasQuestions) {
        this.hasQuestions = hasQuestions
    }

    HasQuestions getParent() {
        return hasQuestions
    }
}
