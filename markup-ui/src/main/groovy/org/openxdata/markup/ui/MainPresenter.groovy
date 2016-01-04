package org.openxdata.markup.ui

import jsyntaxpane.actions.ActionUtils
import org.codehaus.groovy.runtime.StackTraceUtils
import org.openxdata.markup.*
import org.openxdata.markup.exception.ValidationException
import org.openxdata.markup.serializer.MarkupAligner
import org.openxdata.markup.serializer.ODKSerializer
import org.openxdata.markup.serializer.XFormSerializer

import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.filechooser.FileFilter
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import static javax.swing.JOptionPane.WARNING_MESSAGE
import static javax.swing.JOptionPane.YES_NO_OPTION
import static javax.swing.SwingUtilities.invokeLater

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/5/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
class MainPresenter implements DocumentListener {

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

        xfmFilter = [accept        : { File file -> file.name.endsWith('.xfm') || file.isDirectory() },
                     getDescription: { "XForm Markup Files" }] as FileFilter


        form.menuImport.addActionListener { executeSafely { xFormImporter.show() } }

        form.btnGenerateXML.addActionListener { executeSafely { btnGenerateXMLActionPerformed() } }

        form.menuOpen.addActionListener { executeSafely { openFile() } }

        form.menuSave.addActionListener { executeSafely { saveFile() } }

        form.menuNew.addActionListener { executeSafely { newFile() } }

        form.menuAlign.addActionListener { executeSafely { align() } }

        form.btnShowXml.addActionListener { Thread.start { executeSafely { showOxdXML() } } }

        form.btnShowOdkXml.addActionListener { Thread.start { executeSafely { showOdkXML() } } }

        form.chkAutoUpdateTree.addActionListener { toggleDocumentListener() }

        form.formLoader = { loadWithConfirmation(addHeader(it)) }

        form.btnRefreshTree.addActionListener { quickParseStudy() }


        form.frame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                handleWindowCloseOperation()
            }
        })

        allowedAttribs = Attrib.allowedAttributes.collect { '@' + it }
        allowedTypes = Attrib.types.collect { '@' + it }

        //loadSample study
        loadForm(addHeader(Resources.sampleStudy))

        renderHistory()
    }

    def align() {
        def text = form.txtMarkUp.text
        def align = new MarkupAligner(text).align()
        loadForm(align)
    }

    void handleWindowCloseOperation() {

        def text = form.txtMarkUp.text
        if (text == null || text.isEmpty()) {
            doExit()
        }

        def option = JOptionPane.showConfirmDialog(form.frame, "Save File?")

        switch (option) {
            case JOptionPane.CANCEL_OPTION:
                break
            case JOptionPane.OK_OPTION:
                saveFile(); doExit();
                break
            default: doExit()
        }
    }

    private def static doExit() {
        System.exit(0)
    }

    private void loadForm(String markupTxt, boolean reloadTree = true) {
        //first remove the listener before you load the form this is to avoid double parsing of the study
        form.txtMarkUp.document.removeDocumentListener(this)

        form.txtMarkUp.read(new StringReader(markupTxt), 'text/xform')

        //put back the document listener if necessary
        toggleDocumentListener()


        if (reloadTree) quickParseStudy()
    }

    void toggleDocumentListener() {
        if (form.chkAutoUpdateTree.isSelected()) {
            form.txtMarkUp.getDocument().addDocumentListener(this)
        } else {
            form.txtMarkUp.getDocument().removeDocumentListener(this)
        }
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
        loadForm("")
        form.title = "OXD-Markup"

    }

    void saveFile() {
        if (currentFile == null) {
            JFileChooser jc = new JFileChooser()

            jc.fileFilter = xfmFilter

            def selectedFile = currentFile ?: HistoryKeeper.lastAccessedFile?.parentFile
            if (selectedFile)
                jc.selectedFile = selectedFile

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
            HistoryKeeper.registerHistory(file.absolutePath)
            renderHistory()
        } else {
            currentFile.text = form.txtMarkUp.text
        }
    }

    void openFile() {
        JFileChooser jc = new JFileChooser()

        jc.fileFilter = xfmFilter
        def selectedFile = currentFile ?: HistoryKeeper.lastAccessedFile
        if (selectedFile)
            jc.selectedFile = selectedFile
        def option = jc.showOpenDialog(form.frame)

        if (option != JFileChooser.APPROVE_OPTION)
            return

        File f = jc.getSelectedFile()

        mayBeSaveFile()
        openFile(f)
    }

    void btnGenerateXMLActionPerformed() {


        Study study = getParsedStudy()

        XFormSerializer ser = getOxdSerializer()
        def studyXML = ser.toStudyXml(study)

        JFileChooser fc = new JFileChooser()
        fc.fileFilter = [
                accept        : { File f ->
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

        HistoryKeeper.registerHistory(f.absolutePath)
        renderHistory()
    }

    def renderHistory() {
        form.renderHistory(HistoryKeeper.history) { String s ->
            def file = s as File
            if (file.exists()) {
                mayBeSaveFile()
                openFile(file)
            } else {
                JOptionPane.showMessageDialog(form.frame, 'File Does Not Exist', 'ERROR', JOptionPane.ERROR_MESSAGE)
                HistoryKeeper.removeHistory(s)
                renderHistory()
            }
        }
    }

    private Study getParsedStudy(boolean validateUniqueId = true) {
        def result = Util.time("StudyParsing") {
            try {
                Study.validateWithXML.set(form.chkUseXMLValidation.isSelected())
                def text = form.txtMarkUp.text
                def parser = Util.createParser(text)
                def study = parser.study()
                if (validateUniqueId) study = mayBeAddUniqueId(study)
                return study
            } catch (Exception x) {
                invokeLater { form.studyTreeBuilder.showError("Error!! [$x.message]") }
                throw x
            } finally {
                Study.validateWithXML.set(null)
            }

        }
        updateTree(result.value)
        return result.value
    }

    private Study mayBeAddUniqueId(Study study) {
        if (!form.chkEnsureUniqueIdentifier.isSelected()) return study

        def uId = new UniqueIdProcessor(study: study, markup: form.txtMarkUp.text)

        if (uId.hasUniqueIdentifier()) return study


        def forms = uId.getFormsWithOutUniqueId()
        def message = """The Forms Below have no unique IDs. The Questions Should Be Hidden or Invisible
                         |${forms.join('\n')}
                         |Can I add the unique id question?""".stripMargin()

        def answer = JOptionPane.showConfirmDialog(form.frame, message, "WARNING!!", YES_NO_OPTION, WARNING_MESSAGE)

        if (answer != JOptionPane.OK_OPTION) return study

        def newMarkup = uId.addUniqueIdentifier()
        invokeLater { loadForm(newMarkup, false) }
        return Util.createParser(newMarkup).study()
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
        form.studyTreeBuilder.updateTree(study) { IQuestion qn -> selectLine(qn.line) }
        invokeLater { form.studyTreeBuilder.expand(3) }
    }

    private selectLine(int line) {
        def docPosition = ActionUtils.getDocumentPosition(form.txtMarkUp, line, 0)
        def txtLine = ActionUtils.getLineAt(form.txtMarkUp, docPosition)
        if (!txtLine) return

        def lineLength = txtLine.size()
        invokeLater {
            form.txtMarkUp.requestFocusInWindow()
            form.txtMarkUp.select(docPosition, docPosition + lineLength)
        }
    }

    public void insertUpdate(final DocumentEvent e) {
        refreshTreeLater()
    }

    public void removeUpdate(DocumentEvent e) {
        refreshTreeLater()
    }

    public void changedUpdate(DocumentEvent e) {
        refreshTreeLater()
    }

    private updating = false
    private long updateTime = System.currentTimeMillis()
    private TREE_UPDATE_PERIOD = 500

    private void refreshTreeLater() {
        updateTime = System.currentTimeMillis()
        if (isUpdating()) return

        toggleUpdating()
        Thread.start {
            while (lastUpdatePeriod() <= TREE_UPDATE_PERIOD) Thread.sleep(TREE_UPDATE_PERIOD)
            quickParseStudy()
            toggleUpdating()
        }
    }

    private long lastUpdatePeriod() { System.currentTimeMillis() - updateTime }

    private synchronized boolean isUpdating() { updating }

    private synchronized void toggleUpdating() { updating = !updating }

    def executeSafely(Closure closure) {
        try {
            closure.call()
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form.frame, ex.message, 'Error While Generating XML', JOptionPane.ERROR_MESSAGE)
            def t = StackTraceUtils.sanitize(ex)
            t.printStackTrace()
            if (ex instanceof ValidationException) selectLine(ex.line)
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
