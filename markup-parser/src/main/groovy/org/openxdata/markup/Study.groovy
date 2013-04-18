package org.openxdata.markup

import static org.openxdata.markup.Form.*

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
class Study {

    String name

    private List<Form> forms = []

    void addForm(Form form) {
        form.study = this
        validateForm(form)
        forms << form
    }

    void addForms(List<Form> forms) {
        forms.each { addForm(it) }
    }

    void validateForm(Form form) {
        form.allQuestions.each {
            validateSkipLogic(it)
            validateCalculation(it)
            validateValidationLogic(it)
        }
    }

    List<Form> getForms() {
        return new ArrayList<Form>(forms)
    }
}
