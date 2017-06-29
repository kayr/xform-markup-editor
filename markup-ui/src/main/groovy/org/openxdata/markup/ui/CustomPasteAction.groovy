package org.openxdata.markup.ui

import groovy.transform.CompileStatic

import javax.swing.text.DefaultEditorKit
import java.awt.event.ActionEvent

/**
 * Created by user on 6/29/2017.
 */
@CompileStatic
class CustomPasteAction extends DefaultEditorKit.PasteAction {

    MainPresenter presenter

    CustomPasteAction(MainPresenter presenter) {
        super()
        this.presenter = presenter
    }

    public void actionPerformed(ActionEvent e) {
        try {
            presenter.isPasting = true
            super.actionPerformed(e)
        } finally {
            presenter.isPasting = false

        }
    }
}
