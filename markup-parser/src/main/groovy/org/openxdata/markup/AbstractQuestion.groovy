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

    def getQuestionIdx() {
        if (hasQuestions instanceof Form)
            return hasQuestions.questions.indexOf(this) + 1
        else if (hasQuestions instanceof RepeatQuestion)
            return "${hasQuestions.getQuestionIdx()}.${hasQuestions.questions.indexOf(this) + 1}"
    }

    @Override
    void setText(String text) {
        if(text.startsWith('*')){
            text = text[1..text.length()-1]
            required = true
        }
        this.question = text
        def tempBind =    Util.getBindName(text)
        if (!binding)
            binding = tempBind
        if (!type)
            type = Util.getType(tempBind)
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

    Form getParentForm(){
        def form = hasQuestions
        while (!(form instanceof Form)){
               form = hasQuestions.parent
        }
        return form
    }
}
