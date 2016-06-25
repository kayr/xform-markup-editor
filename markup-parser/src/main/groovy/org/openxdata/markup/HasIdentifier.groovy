package org.openxdata.markup

import groovy.transform.CompileStatic


@CompileStatic
trait HasIdentifier {
    String name
    String id
    int line


    HasQuestions parent

    void setName(String name) {
        this.name = name
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

}
