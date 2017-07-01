package org.openxdata.markup.ui

import jsyntaxpane.actions.gui.ComboCompletionDialog
import org.openxdata.markup.Attrib
import org.openxdata.markup.Form
import org.openxdata.markup.transformers.TransformerResolver

import javax.swing.text.JTextComponent

/**
 * Created by user on 6/19/2017.
 */
class CompletionDialog {

    JTextComponent        txtTarget
    ComboCompletionDialog dialog

    CompletionDialog(JTextComponent txtTarget) {
        this.txtTarget = txtTarget
        this.dialog = new ComboCompletionDialog(txtTarget)
    }

    void showForIds(Form form) {

        def ids = form.allElements*.binding

        dialog.displayFor('@', ids)

    }

    void showForAnnotations() {
        dialog.displayFor('', ids as List<String>)
    }

    private def ids = Attrib.allowedAttributes + Attrib.types + TransformerResolver.instance.registeredAnnotations()

}
