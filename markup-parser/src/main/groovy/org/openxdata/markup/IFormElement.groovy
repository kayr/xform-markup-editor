package org.openxdata.markup

import groovy.transform.CompileStatic
import org.openxdata.markup.exception.ValidationException


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

        return "$firstInstanceParent.absoluteBinding/$binding"
    }

    Form getParentForm() {
        def parent = this
        while (!(parent instanceof Form)) {
            parent = parent.parent
        }
        return parent as Form
    }

    HasQuestions getFirstInstanceParent() {
        def parent = this.parent
        while (parent && !parent.binding) {
            parent = parent.parent
        }
        return parent
    }

    def getContextIdx() {
        return "${parent.questions.indexOf(this) + 1}"
    }


    String getIndexedAbsoluteBinding() {
        if (!parent)
            return "/$binding"
        return "$firstInstanceParent.indexedAbsoluteBinding/$indexedBinding"
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
        parentForm.getIndex(this)
    }


    String getNumberedText() {
        def idx = getQuestionIdx()
        return idx ? "${idx}. $name" : name
    }

    String getText() {
        return name
    }

    String getText(boolean number) {
        if (number)
            return getNumberedText()
        return text

    }


    void validateIdExistsIfNecessary() {
        def DEFAULT_BIND_ATTRS = [visible: true]
        def shouldHaveId = binding == null && (_bindAttr != DEFAULT_BIND_ATTRS || bindAttributes.size() > 0)
        if (shouldHaveId)
            throw new ValidationException("Element[$this] Has No Id But Has Logic Attributes", line)

    }

}