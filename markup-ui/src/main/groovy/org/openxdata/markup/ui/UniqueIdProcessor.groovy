package org.openxdata.markup.ui

import org.openxdata.markup.Form
import org.openxdata.markup.Study
import org.openxdata.markup.Util

/**
 * Created by kay on 11/10/2015.
 */
class UniqueIdProcessor {

    Study study
    String markup

    def addUniqueIdentifier() {

        //incase the form is one
        def formSize = study.forms.size()
        if (formSize == 1) {
            return markup + '\n' + Resources.uniqueIdQuestion
        }

        for (int i = 0; i < formSize; i++) {
            def curForm = study.forms[i]
            if (!hasUniqueIdentifier(curForm)) {
                Form nextForm = null

                if (i < formSize - 1) {
                    nextForm = study.forms[i + 1]
                }

                if (nextForm) {
                    insertUniqueQuestionAbove(nextForm)
                } else {
                    markup = markup + '\n' + Resources.uniqueIdQuestion
                }
            }
        }

        return markup
    }

    //insert a question just above this form
    private def insertUniqueQuestionAbove(Form nextForm) {
        def line = nextForm.startLine
        def lines = markup.split(/(\r)*\n/) as List<String>
        lines.add(line - 1, Resources.uniqueIdQuestion)
        markup = lines.join('\n')
        quickParse()
    }

    private def quickParse() {
        try {
            Study.quickParse.set(true)
            def parser = Util.createParser(markup)
            study = parser.study()
        } finally {
            Study.quickParse.set(false)
        }
    }

    boolean hasUniqueIdentifier() {
        return study.forms.every { Form form -> hasUniqueIdentifier(form) }
    }

    List<Form> getFormsWithOutUniqueId() {
        study.forms.findAll { !hasUniqueIdentifier(it) }
    }

    static boolean hasUniqueIdentifier(Form form) {
        form.questions.any {
            it.binding == 'unique_id' &&
                    it.calculation?.replaceAll(/\s+/, '') == "once(concat('uuid:',uuid()))"
        }
    }

}
