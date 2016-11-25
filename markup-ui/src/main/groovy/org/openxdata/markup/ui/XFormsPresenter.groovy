package org.openxdata.markup.ui

import org.openxdata.markup.Form

import javax.swing.*

import static org.openxdata.markup.ui.IOHelper.chooseFile
import static org.openxdata.markup.ui.IOHelper.filter

/**
 * Created by kay on 10/7/2016.
 */
class XFormsPresenter {

    JFrame parent
    XFormsUI previewFrame

    XFormsPresenter(JFrame parent) {
        this.parent = parent
        init()
    }


    def init() {
        previewFrame = new XFormsUI(parent)

        previewFrame.btnSave.addActionListener {
            saveSelectedFile()
        }
    }


    void renderXMLPreview(Map<Form, String> xforms) {
        xforms.each { frmName, xml ->
            previewFrame.addTab("${frmName.name}", xml)
        }
    }

    void saveSelectedFile() {
        def view = previewFrame.getSelectedView()
        def lastAccessedDirectory = HistoryKeeper.lastAccessedDirectory
        def suggestedFile = new File(lastAccessedDirectory, "${view.name}.xform.xml")
        def currentXML = view.text

        def file = chooseFile(lastAccessedDirectory,
                filter('XML Files', 'xml')) { JFileChooser jc ->
            jc.setSelectedFile(suggestedFile)
            jc.showSaveDialog(previewFrame.frame)
        }

        if (!file) return

        if (file.exists()) {
            def answer = JOptionPane.showConfirmDialog(
                    previewFrame.frame,
                    "Overwrite File [${file.name}]?",
                    "OVERWRITE?",
                    JOptionPane.WARNING_MESSAGE)
            if (answer != JOptionPane.OK_OPTION) return

        }

        IOHelper.save file, currentXML
    }

}
