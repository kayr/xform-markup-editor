package org.openxdata.markup

import groovy.transform.CompileStatic


@CompileStatic
trait IFormElement {
    String name
    String id
    int line
    boolean hasAbsoluteId = false
    String skipAction

    //use this map to store common bind attributes to work around groovy trait bug
    //where traits with > 10 fields cannot compile
    private Map _bindAttr = [visible: true]

    //this will store any other extra bind attributes
    Map bindAttributes = [:]


    Map<String, String> layoutAttributes = [:]

    HasQuestions parent

    void setName(String name) {
        this.name = name
    }

    void setSkipLogic(String string) {
        _bindAttr.skipLogic = string
    }

    String getSkipLogic() {
        return _bindAttr.skipLogic
    }

    void setMessage(String string) {
        _bindAttr.message = string
    }

    String getMessage() {
        return _bindAttr.message
    }

    void setValidationLogic(String string) {
        _bindAttr.validationLogic = string
    }

    String getValidationLogic() {
        return _bindAttr.validationLogic
    }

    void setVisible(boolean visible) {
        _bindAttr.visible = visible
    }

    boolean isVisible() {
        return _bindAttr.visible
    }


    String getBinding() {
        if (!this.id)
            mayBeSetBind()
        return this.id
    }

    void setBinding(String binding) {
        this.id = binding
    }

    private mayBeSetBind() {
        if (!this.id)
            this.id = Util.getBindName(name)
    }

    String getAbsoluteBinding() {
        if (!parent)
            return "/$binding"

        return "$parent.absoluteBinding/$binding"
    }

    Form getParentForm() {
        def parent = this
        while (!(parent instanceof Form)) {
            parent = parent.parent
        }
        return parent as Form
    }

    def getContextIdx() {
        return "${parent.questions.indexOf(this) + 1}"
    }


    String getIndexedAbsoluteBinding() {
        if (parent instanceof IQuestion)
            return "$parent.indexedAbsoluteBinding/$indexedBinding"
        return "$parent.absoluteBinding/$indexedBinding"
    }

    String getIndexedBinding() {
        if (hasAbsoluteId)
            return binding
        return "_${questionIdx.replace('.', '_')}$binding"
    }

    String getBinding(boolean numbered) {
        if (numbered)
            return getIndexedBinding()
        return binding
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

    String getQuestionIdx() {
        if (parent instanceof Page)
            return '' + (parent.parentForm.questions.indexOf(this) + 1)
        else if (parent instanceof IQuestion)
            return "${parent.getQuestionIdx()}.${parent.questions.indexOf(this) + 1}"
    }

    String getNumberedText() {
        return "${getQuestionIdx()}. $name"
    }

    String getText() {
        return name
    }

}
