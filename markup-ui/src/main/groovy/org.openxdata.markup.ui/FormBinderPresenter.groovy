package org.openxdata.markup.ui

import org.openxdata.markup.Attrib
import org.openxdata.markup.Form
import org.openxdata.markup.Study
import org.openxdata.markup.Util
import org.openxdata.markup.serializer.XFormSerializer

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileFilter

import static javax.swing.JOptionPane.YES_NO_OPTION

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/5/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
class FormBinderPresenter {

    MarkupForm form
    javax.swing.filechooser.FileFilter xfmFilter
    File currentFile
    def allowedAttribs
    def allowedTypes


    FormBinderPresenter() {
        form = new MarkupForm()
        form.title = "OXD-Markup"

        form.setLocationByPlatform(true);
        init()
    }

    void init() {

        xfmFilter = [
                accept: {File file -> file.name.endsWith('.xfm') || file.isDirectory() },
                getDescription: {"XForm Markup Files"}
        ] as javax.swing.filechooser.FileFilter

        form.btnGenerateXML.addActionListener({ ActionEvent evt ->

            executeSafely { btnGenerateXMLActionPerformed(evt)};

        } as ActionListener)

        form.menuOpen.addActionListener({ActionEvent evt ->
            executeSafely { openFile()}
        } as ActionListener)

        form.menuSave.addActionListener({ActionEvent evt ->
            executeSafely { saveFile()}
        } as ActionListener)

        form.menuNew.addActionListener({ActionEvent evt ->
            executeSafely { newFile()}
        } as ActionListener)

        form.btnShowXml.addActionListener({ActionEvent evt ->
            executeSafely {showXML()}
        } as ActionListener)

        form.menuAdvancedSkip.addActionListener({
            loadSampleWithConfirmation(addHeader(Resources.advanceSkip))
        } as ActionListener)

        form.menuSimpleSkip.addActionListener({
            loadSampleWithConfirmation(addHeader(Resources.simpleSkip))
        } as ActionListener)

        form.menuOxdForm.addActionListener({
            loadSampleWithConfirmation(addHeader(Resources.oxdSampleForm))
        } as ActionListener)


        form.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                handleWindowCloseOperation(evt)
            }
        })


        allowedAttribs = Attrib.allowedAttributes.collect {'@' + it}
        allowedTypes = Attrib.types.collect {'@' + it}

        //loadSample study
        loadForm(addHeader(Resources.sampleStudy))
    }

    void handleWindowCloseOperation(WindowEvent evt) {

        def text = form.txtMarkUp.text
        if (text == null || text.isEmpty()) {
            System.exit(0)
        }

        def option = JOptionPane.showConfirmDialog(form, "Save File?")

        switch (option) {
            case JOptionPane.CANCEL_OPTION:
                return
            case JOptionPane.OK_OPTION:
                saveFile()
            default:
                System.exit(0)
        }
    }

    private void loadForm(String markupTxt) {
        form.txtMarkUp.read(new StringReader(markupTxt), 'text/xform')
    }



    private void loadSampleWithConfirmation(String markupTxt) {
        def option = JOptionPane.showConfirmDialog(form, "Are You Sure you want to load this form?", 'Confirm', YES_NO_OPTION)

        if (option == JOptionPane.OK_OPTION) {
            reset()
            loadForm(markupTxt)
        }
    }

    void showXML() {

        def study = getParsedStudy()
        XFormSerializer ser = new XFormSerializer(numberQuestions: true)
        def studyXml = ser.toStudyXml(study)

        def previewFrame = XFormView.initFrame(form)
        ser.xforms.each {frmName, xml ->
            previewFrame.addLockedEditor("Frm:$frmName.name", xml)
        }

        previewFrame.addLockedEditor("Study:$study.name", studyXml)

    }

    void newFile() {

        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(form, "Are You Sure", 'Confirm', YES_NO_OPTION)) {
            return
        }

        mayBeSaveFile()

        reset()
    }

    private void mayBeSaveFile() {
        if (currentFile != null && JOptionPane.showConfirmDialog(form, "Save File First?", 'Confirm', YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            currentFile.text = form.txtMarkUp.text
        }
    }

    private void reset() {
        currentFile = null
        form.txtMarkUp.text = ""
        form.title = "OXD-Markup"
    }

    void saveFile() {
        if (currentFile == null) {
            JFileChooser jc = new JFileChooser()

            jc.fileFilter = xfmFilter

            def option = jc.showSaveDialog(form)

            if (option != JFileChooser.APPROVE_OPTION)
                return

            File file = jc.getSelectedFile()

            if (!file.name.endsWith('.xfm')) {
                file = new File(file.absolutePath + '.xfm')
            }


            file.text = form.txtMarkUp.text

            form.title = "OXD-Markup: " + file.absolutePath
            currentFile = file
        }
        else {
            currentFile.text = form.txtMarkUp.text
        }
    }

    void openFile() {
        JFileChooser jc = new JFileChooser()

        jc.fileFilter = xfmFilter
        if (currentFile)
            jc.selectedFile = currentFile
        def option = jc.showOpenDialog(form)

        if (option != JFileChooser.APPROVE_OPTION)
            return

        File f = jc.getSelectedFile()

        mayBeSaveFile()
        openFile(f)
    }

    void btnGenerateXMLActionPerformed(ActionEvent actionEvent) {


        Study study = getParsedStudy()

        XFormSerializer ser = new XFormSerializer()
        def studyXML = ser.toStudyXml(study)

        JFileChooser fc = new JFileChooser()
        fc.fileFilter = [
                accept: { File f ->
                    return f.name.endsWith('.xml') || f.isDirectory()
                },
                getDescription: {
                    return 'XML File'
                }
        ] as FileFilter
        fc.setSelectedFile(new File(study.name))
        def option = fc.showSaveDialog(form)


        if (option != JFileChooser.APPROVE_OPTION)
            return

        def file = fc.selectedFile
        if (!file.name.endsWith('.xml')) {
            file = new File(file.absolutePath + '.xml')
        }

        file.text = studyXML

        def formFolder = new File(file.parentFile.absolutePath + "/xforms")
        println "Creating directory $formFolder.absolutePath"
        formFolder.mkdirs()

        ser.xforms.each {key, value ->
            new File(formFolder, key.name + '.xml').text = value
        }

        def msg = """Created study file $file.absolutePath
Created ${ser.xforms.size()} Xform file(s) in folder $formFolder.absolutePath """
        JOptionPane.showMessageDialog(form, msg)
    }

    void openFile(File f) {

        def text = f.text

        form.txtMarkUp.read(new StringReader(text), 'text/xform');

        currentFile = f
        form.title = "OXD-Markup: " + currentFile.absolutePath

    }

    private Study getParsedStudy() {
        def result = Util.time("StudyParsing") {
            def text = form.txtMarkUp.text
            def parser = Util.createParser(text)
            def study = parser.study()
            return study
        }
        return result.value
    }

    def executeSafely(Closure closure) {
        try {
            closure.call()
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form, ex.message, 'Error While Generating XML', JOptionPane.ERROR_MESSAGE)
            ex.printStackTrace()
        }

    }

    String addHeader(String markupTxt) {
        return """//Allowed Attributes: $allowedAttribs
//Allowed Types: $allowedTypes
//Use Ctrl + K for auto-completion
$markupTxt"""
    }

    static main(args) {
        new FormBinderPresenter().form.setVisible(true)
    }

}
