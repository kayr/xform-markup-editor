package org.openxdata.markup.ui

import jsyntaxpane.actions.ActionUtils
import org.openxdata.markup.*
import org.openxdata.markup.serializer.MarkupAligner
import org.openxdata.markup.serializer.ODKSerializer
import org.openxdata.markup.serializer.XFormSerializer

import javax.swing.*
import javax.swing.filechooser.FileFilter
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import static javax.swing.JOptionPane.YES_NO_OPTION
import static javax.swing.SwingUtilities.invokeLater

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/5/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
class MainPresenter {

    XFormImporterPresenter xFormImporter
    MainUI form
    File currentFile
    def xfmFilter
    def allowedAttribs
    def allowedTypes


    MainPresenter() {
        form = new MainUI()

        xFormImporter = new XFormImporterPresenter(this)
        init()
    }

    void init() {

        xfmFilter = [
                accept: { File file -> file.name.endsWith('.xfm') || file.isDirectory() },
                getDescription: { "XForm Markup Files" }
        ] as FileFilter


        form.menuImport.addActionListener({
            executeSafely { xFormImporter.show() }
        } as ActionListener)

        form.btnGenerateXML.addActionListener({ ActionEvent evt ->
            executeSafely { btnGenerateXMLActionPerformed(evt) };
        } as ActionListener)

        form.menuOpen.addActionListener({ ActionEvent evt ->
            executeSafely { openFile() }
        } as ActionListener)

        form.menuSave.addActionListener({ ActionEvent evt ->
            executeSafely { saveFile() }
        } as ActionListener)

        form.menuNew.addActionListener({ ActionEvent evt ->
            executeSafely { newFile() }
        } as ActionListener)

        form.menuAlign.addActionListener({ ActionEvent evt ->
            executeSafely { align() }
        } as ActionListener)

        form.btnShowXml.addActionListener({ ActionEvent evt ->
            Thread.start { executeSafely { showOxdXML() } }
        } as ActionListener)

        form.btnShowOdkXml.addActionListener({ ActionEvent evt ->
            Thread.start { executeSafely { showOdkXML() } }
        } as ActionListener)

        form.formLoader = {
            loadWithConfirmation(addHeader(it))
        }


        form.frame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                handleWindowCloseOperation()
            }
        })

        form.btnRefreshTree.addActionListener({
            quickParseStudy()
        } as ActionListener)


        allowedAttribs = Attrib.allowedAttributes.collect { '@' + it }
        allowedTypes = Attrib.types.collect { '@' + it }

        //loadSample study
        loadForm(addHeader(Resources.sampleStudy))
    }

    def align() {
        def text = form.txtMarkUp.text
        def align = new MarkupAligner(text).align()
        loadForm(align)
    }

    void handleWindowCloseOperation() {

        def text = form.txtMarkUp.text
        if (text == null || text.isEmpty()) {
            System.exit(0)
        }

        def option = JOptionPane.showConfirmDialog(form.frame, "Save File?")

        switch (option) {
            case JOptionPane.CANCEL_OPTION: break;
            case JOptionPane.OK_OPTION: saveFile(); System.exit(0); break;
            default: System.exit(0)
        }
    }

    private void loadForm(String markupTxt) {
        form.txtMarkUp.read(new StringReader(markupTxt), 'text/xform')
        quickParseStudy()
    }


    void loadWithConfirmation(String markupTxt) {
        def option = JOptionPane.showConfirmDialog(form.frame, "Are You Sure you want to load this form?", 'Confirm', YES_NO_OPTION)

        if (option == JOptionPane.OK_OPTION) {
            mayBeSaveFile()
            reset()
            loadForm(markupTxt)
        }
    }


    void showOxdXML() {
        def study = getParsedStudy()
        XFormSerializer ser = getOxdSerializer()
        ser.toStudyXml(study)
        renderXMLPreview(ser.xforms)
    }

    void showOdkXML() {
        def study = getParsedStudy()
        def ser = getODKSerializer()
        ser.toStudyXml(study)
        renderXMLPreview(ser.xforms)
    }

    private void renderXMLPreview(Map<Form, String> xforms) {
        def previewFrame = new XFormsUI(form.frame)
        xforms.each { frmName, xml ->
            previewFrame.addTab("Form:$frmName.name", xml)
        }
    }

    private XFormSerializer getOxdSerializer() {
        new XFormSerializer(
                numberQuestions: form.chkNumberLabels.model.isSelected(),
                numberBindings: form.chkNumberBindings.isSelected(),
                generateView: form.chkGenerateLayout.isSelected()
        )
    }

    private ODKSerializer getODKSerializer() {
        new ODKSerializer(
                numberQuestions: form.chkNumberLabels.model.isSelected(),
                numberBindings: form.chkNumberBindings.isSelected(),
                oxdConversion: form.chkEmulateOXDConversion.isSelected()
        )
    }

    void newFile() {

        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(form.frame, "Are You Sure", 'Confirm', YES_NO_OPTION)) {
            return
        }

        mayBeSaveFile()

        reset()
    }

    private boolean mayBeSaveFile() {
        if (!doesFileNeedSaving() && JOptionPane.showConfirmDialog(form.frame, "Save File First?", 'Confirm', YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            currentFile.text = form.txtMarkUp.text
            return true
        }
        return false
    }

    boolean doesFileNeedSaving() {
        return currentFile == null || currentFile.text == form.txtMarkUp.text
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

            def option = jc.showSaveDialog(form.frame)

            if (option != JFileChooser.APPROVE_OPTION)
                return

            File file = jc.getSelectedFile()

            if (!file.name.endsWith('.xfm')) {
                file = new File(file.absolutePath + '.xfm')
            }


            file.text = form.txtMarkUp.text

            form.title = "OXD-Markup: " + file.absolutePath
            currentFile = file
        } else {
            currentFile.text = form.txtMarkUp.text
        }
    }

    void openFile() {
        JFileChooser jc = new JFileChooser()

        jc.fileFilter = xfmFilter
        if (currentFile)
            jc.selectedFile = currentFile
        def option = jc.showOpenDialog(form.frame)

        if (option != JFileChooser.APPROVE_OPTION)
            return

        File f = jc.getSelectedFile()

        mayBeSaveFile()
        openFile(f)
    }

    void btnGenerateXMLActionPerformed(ActionEvent actionEvent) {


        Study study = getParsedStudy()

        XFormSerializer ser = getOxdSerializer()
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
        def option = fc.showSaveDialog(form.frame)


        if (option != JFileChooser.APPROVE_OPTION)
            return

        def file = fc.selectedFile
        if (!file.name.endsWith('.xml')) {
            file = new File(file.absolutePath + '.xml')
        }

        file.text = studyXML

        def formFolder = createDirectory(file.parentFile.absolutePath + "/xforms")
        ser.xforms.each { key, value ->
            new File(formFolder, key.name + '.xml').text = value
        }

        def importsFolder = createDirectory(file.parentFile.absolutePath + "/xform-imports")
        def imports = ser.formImports
        imports.each { key, value ->
            new File(importsFolder, key + '.xml').text = value
        }

        def msg = "Created study file $file.absolutePath\n" +
                "Created ${ser.xforms.size()} Xform file(s) in folder $formFolder.absolutePath\n" +
                "Created ${imports.size()} import file(s) in folder $importsFolder.absolutePath"
        JOptionPane.showMessageDialog(form.frame, msg)
    }

    static File createDirectory(String path) {
        def directory = new File(path)
        println "Creating directory $directory.absolutePath"
        directory.mkdirs()
        return directory
    }

    void openFile(File f) {

        def text = f.text
        System.setProperty('form.dir', f.parent)

        loadForm(text)

        currentFile = f
        form.title = "OXD-Markup: " + currentFile.absolutePath

    }

    private Study getParsedStudy() {
        def result = Util.time("StudyParsing") {
            try {
                def text = form.txtMarkUp.text
                def parser = Util.createParser(text)
                def study = parser.study()
                return study
            } catch (Exception x) {
                invokeLater { form.studyTreeBuilder.showError("Error!! [$x.message]") }
                throw x
            }
        }
        updateTree(result.value)
        return result.value
    }

    void quickParseStudy() {
        Thread.start {
            Study.quickParse.set(true)
            try {
                getParsedStudy()
            } catch (Exception x) {
                println('error parsing study')
                invokeLater { form.studyTreeBuilder.showError("Error!! [$x.message]") }
            }
        }
    }

    def updateTree(Study study) {
        form.studyTreeBuilder.updateTree(study) { IQuestion qn -> selectQn(qn) }
        invokeLater { form.studyTreeBuilder.expand(3) }
    }

    private selectQn(IQuestion qn) {
        def docPosn = ActionUtils.getDocumentPosition(form.txtMarkUp, qn.line, 0)
        def txtLine = ActionUtils.getLineAt(form.txtMarkUp, docPosn)
        if (!txtLine) return

        def lineLength = txtLine.size()
        invokeLater {
            form.txtMarkUp.requestFocusInWindow()
            form.txtMarkUp.select(docPosn, docPosn + lineLength)
        }
    }

    def executeSafely(Closure closure) {
        try {
            closure.call()
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form.frame, ex.message, 'Error While Generating XML', JOptionPane.ERROR_MESSAGE)
            ex.printStackTrace()
        }

    }

    String addHeader(String markupTxt) {
        return "//Allowed Attributes: $allowedAttribs\n" +
                "//Allowed Types: $allowedTypes\n" +
                "//Use Ctrl + K for auto-completion\n$markupTxt"
    }

    static main(args) {
        new MainPresenter()

    }

}
