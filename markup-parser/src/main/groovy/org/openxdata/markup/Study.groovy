package org.openxdata.markup
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
class Study {

    /**
     * Set this to true. So the the parser does not build the whole CSV list
     */
    public static ThreadLocal<Boolean> quickParse = new ThreadLocal<Boolean>()
    public static ThreadLocal<Boolean> validateWithXML = new ThreadLocal<Boolean>()

    String name
    boolean validating = true

    private List<Form> forms = []

    Study() {}

    Study(boolean validating) {
        this.validating = validating
    }

    void addForm(Form form) {
        form.study = this
        if (validating) form.validate()
        forms << form
    }

    void addForms(List<Form> forms) {
        forms.each { addForm(it) }
    }

    List<Form> getForms() {
        return new ArrayList<Form>(forms)
    }

    String toString() {
        name
    }
}
