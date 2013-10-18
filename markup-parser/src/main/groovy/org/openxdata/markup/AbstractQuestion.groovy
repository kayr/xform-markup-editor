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
    int line


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
            return ''+(hasQuestions.questions.indexOf(this) + 1)
        else if (hasQuestions instanceof RepeatQuestion)
            return "${hasQuestions.getQuestionIdx()}.${hasQuestions.questions.indexOf(this) + 1}"
    }

    def getContextIdx(){
        return ''+(hasQuestions.questions.indexOf(this) + 1)
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
    String getAbsoluteBinding() {
        return "$hasQuestions.absoluteBinding/$binding"
    }

     @Override
     String getIndexedAbsoluteBinding() {
         if (hasQuestions instanceof IQuestion)
             return "$hasQuestions.indexedAbsoluteBinding/$indexedBinding"
         return "$hasQuestions.absoluteBinding/$indexedBinding"
     }

    String getRelativeBinding() {
        return absoluteBinding.replaceFirst('/', '')
    }

    String getIndexedRelativeBinding() {
        return indexedAbsoluteBinding.replaceFirst('/', '')
    }

     @Override
     String getIndexedBinding() {
         if(binding == 'endtime' && (type == 'dateTime' || type == 'time'))
             return binding
         return "_${questionIdx.replace('.','_')}$binding"
     }

    String getBinding(boolean  numbered){
        if(numbered)
            return getIndexedBinding()
        return binding
    }

    String getText(boolean number){
        if(number)
            return getNumberedText()
        return text

    }

    String getNumberedText() {
        return getQuestionIdx() +". $text"
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
               form = hasQuestions.parentForm
        }
        return form
    }

    String toString(){
        return "$questionIdx.($binding)$text"
    }
}
