package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractQuestion implements IQuestion, HasIdentifier,HasLayoutAttributes,HasBindAttributes {

    String comment
    boolean readOnly
    String type
    boolean required
    boolean visible = true
    String skipLogic
    String skipAction
    String validationLogic
    String message
    String calculation
    boolean hasAbsoluteId = false
    def value
    //todo test not setting conflicting attributes eg bind attributes cannot have bind,locked,relevant etc


    AbstractQuestion() {

    }

    AbstractQuestion(String question) {
        setText(question)
    }

    @Override
    String getText() {
        return name
    }

    def getQuestionIdx() {
        if (parent instanceof Page)
            return '' + (parent.parentForm.questions.indexOf(this) + 1)
        else if (parent instanceof IQuestion)
            return "${parent.getQuestionIdx()}.${parent.questions.indexOf(this) + 1}"
    }

    def getContextIdx() {
        return "${parent.questions.indexOf(this) + 1}"
    }

    @Override
    void setText(String text) {
        if (text.startsWith('*')) {
            text = text[1..text.length() - 1]
            required = true
        }
        this.name = text
        def tempBind = Util.getBindName(text)
        if (!binding)
            binding = tempBind
        if (!type)
            type = Util.getType(tempBind)
    }

    void setHasAbsoluteId(boolean hasAbsoluteId) {
        this.hasAbsoluteId = hasAbsoluteId
    }

    boolean getHasAbsoluteId() {
        return hasAbsoluteId
    }


    @Override
    String getIndexedAbsoluteBinding() {
        if (parent instanceof IQuestion)
            return "$parent.indexedAbsoluteBinding/$indexedBinding"
        return "$parent.absoluteBinding/$indexedBinding"
    }

    String getRelativeBinding() {
        return absoluteBinding.replaceFirst('/', '')
    }

    String getIndexedRelativeBinding() {
        return indexedAbsoluteBinding.replaceFirst('/', '')
    }

    @Override
    String getIndexedBinding() {
        if ((binding == 'endtime' && (type == 'dateTime' || type == 'time')) || hasAbsoluteId)
            return binding
        return "_${questionIdx.replace('.', '_')}$binding"
    }

    String getBinding(boolean numbered) {
        if (numbered)
            return getIndexedBinding()
        return binding
    }

    String getText(boolean number) {
        if (number)
            return getNumberedText()
        return text

    }

    String getNumberedText() {
        return "${getQuestionIdx()}. $text"
    }

    @Override
    String getComment() {
        return comment
    }


    String getAbsoluteBinding(boolean indexed, boolean relative) {
        def var = getAbsoluteBinding()
        if (indexed) {
            var = getIndexedAbsoluteBinding()
        }

        if (relative) {
            var = var.replaceFirst('/', '')
        }

        return var

    }

    String toString() {
        return "$questionIdx. $text"
    }
}
