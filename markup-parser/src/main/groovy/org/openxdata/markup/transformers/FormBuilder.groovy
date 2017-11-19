package org.openxdata.markup.transformers

import groovy.transform.CompileStatic
import org.openxdata.markup.*

/**
 * Created by user on 6/30/2017.
 */
@CompileStatic
class FormBuilder {

    IFormElement elem = new TextQuestion(transformAdded: true)

    static FormBuilder create() {
        new FormBuilder()
    }

    FormBuilder textQn() {
        elem = new TextQuestion(transformAdded: true)
        return this
    }

    FormBuilder groupQn() {
        elem = new Page(transformAdded: true)
        this
    }

    FormBuilder addElements(IFormElement... elements) {
        for (elem in elements) {
            group().addElement(elem)
        }
        this
    }

    Page group() {
        elem as Page
    }

    FormBuilder text(String txt) {
        castToQuestion().setText(txt)
        return this
    }

    FormBuilder binding(String binding) {
        elem.setId(binding)
        return this
    }

    FormBuilder calculation(String expr) {
        castToQuestion().calculation = expr
        return this
    }

    FormBuilder showIf(String skipLogic) {
        addSkipLogic('show', skipLogic)
        return this
    }

    FormBuilder enableIf(String skipLogic) {
        addSkipLogic('enable', skipLogic)
        return this
    }

    FormBuilder disableIf(String skipLogic) {
        addSkipLogic('disable', skipLogic)
        return this
    }

    FormBuilder hideIf(String skipLogic) {
        addSkipLogic('hide', skipLogic)
        return this
    }


    FormBuilder addSkipLogic(String action, String expr) {
        castToQuestion().skipLogic = expr
        castToQuestion().skipAction = action
        return this
    }

    FormBuilder visible(boolean visible) {
        elem.visible = visible
        return this
    }

    FormBuilder meta(TransformAttribute attribute) {
        elem.line = attribute.line
        return this
    }

    FormBuilder line(int line) {
        elem.line = line
        return this
    }

    FormBuilder type(XformType type) {
        castToQuestion().xformType = type
        castToQuestion().type = type.value
        this
    }

    FormBuilder bindAttr(String name, String value) {
        elem.bindAttributes.put(name, value)
        this
    }

    FormBuilder layoutAttr(String name, String value) {
        elem.layoutAttributes.put(name, value)
        this

    }

    FormBuilder absolute(boolean absolute) {
        elem.setHasAbsoluteId(absolute)
        this

    }

    private IQuestion castToQuestion() {
        (IQuestion) elem
    }

    IQuestion asQuestion() { (IQuestion) elem }

    ISelectionQuestion asSelectQuestion() {
        (ISelectionQuestion) elem
    }

    def <T> T getAs(T) { (T) elem }
}



