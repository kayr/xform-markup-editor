package org.openxdata.markup.ui

import fr.hugo4715.oslib.AbstractOperatingSystem
import fr.hugo4715.oslib.OperatingSystem
import groovy.swing.SwingBuilder
import jsyntaxpane.DefaultSyntaxKit
import jsyntaxpane.actions.CaretMonitor
import jsyntaxpane.components.LineNumbersRuler
import jsyntaxpane.components.SyntaxComponent
import org.openxdata.markup.ReflectionUtils

import javax.swing.*
import javax.swing.text.DefaultStyledDocument
import java.awt.*
import java.awt.event.ActionEvent

import static javax.swing.SwingUtilities.invokeLater
import static javax.swing.UIManager.getIcon
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

/**
 * Created by kay on 7/11/14.
 */
class MainUI {

    //UIManager.getDefaults().findAll{it.key.toString().toLowerCase().endsWith('icon')}
    static Icon ICON_OPEN = getIcon('Tree.openIcon')
    static Icon ICON_SAVE = getIcon('FileView.floppyDriveIcon')
    static Icon ICON_NEW = getIcon('FileView.fileIcon')
    static Icon ICON_IMPORT = getIcon('FileChooser.upFolderIcon')
    static Icon ICON_FORM = getIcon('FileChooser.detailsViewIcon')
    static Icon ICON_REFRESH = createImageIcon('/refresh.png', 'Refresh')

    Closure formLoader
    SwingBuilder s
    PrintStream info

    MainUI() {
        initSyntaxKit()
        initUi()

    }

    def initSyntaxKit() {
        DefaultSyntaxKit.initKit()
        // override default syntax values
        def config = DefaultSyntaxKit.getConfig(DefaultSyntaxKit.class);

        def preferredFonts = ["Consolas 12", "Monospaced 13", "Courier New 12"]
        def os = OperatingSystem.getOperatingSystem()
        if (os.isUnix()) {
            //Linux atleast on UBUNTU does not seem to like Consolas
            preferredFonts = ["Monospaced 13", "Courier New 12"]
        }

        for (it in (preferredFonts)) {
            if (Font.decode(it)) {
                println("Default Font Set To: [$it]")
                config.put("DefaultFont", it)
                break
            }
        }

        registerXformType()
    }

    def registerXformType() {
        DefaultSyntaxKit.registerContentType("text/xform", "org.openxdata.markup.ui.XFormMarkupSyntaxKit")
    }

    def initUi() {
        s = new SwingBuilder()

        def _menuBar = {
            s.menuBar() {

                menu(text: 'File', mnemonic: 'F') {
                    menuOpen = menuItem(text: 'Open', mnemonic: 'O', icon: ICON_OPEN)
                    menuSave = menuItem(text: 'Save', mnemonic: 'S', icon: ICON_SAVE)

                    separator()
                    menuRecent = menu(text: 'Recent', font: new Font(Font.SANS_SERIF, Font.BOLD, 12), icon: ICON_FORM)
                    separator()

                    menuNew = menuItem(text: 'New', mnemonic: 'N', icon: ICON_NEW)
                    menuImport = menuItem(text: 'Import', mnemonic: 'I', icon: ICON_IMPORT)


                }



                menu(text: 'Tools', mnemonic: 'T') {
                    menuAlign = menuItem(text: 'Align Text', mnemonic: 'A', icon: ICON_FORM)
                }

                menu(text: 'Preferences') {

                    menuEnableODKMode = menuItem(text: 'Enable ODK Mode')
                    separator()
                    menuItem(text: '  General:', font: new Font(Font.SANS_SERIF, Font.BOLD, 12))
                    separator()
                    chkNumberBindings = checkBoxMenuItem(text: 'Number IDs')
                    chkUseXMLValidation = checkBoxMenuItem(text: 'Allow Invalid OXD IDs(OpenXData Lower Case Ids Only)')
                    chkEnsureUniqueIdentifier = checkBoxMenuItem(text: 'Ensure Unique Identifier Question Exists', selected: true)
                    chkEnableAutoSave = checkBoxMenuItem(text: 'AutoSave every 5mins', selected: true)


                    separator()
                    menuItem(text: '  OpenData Kit:', font: new Font(Font.SANS_SERIF, Font.BOLD, 12))
                    separator()
                    chkEmulateOXDConversion = checkBoxMenuItem(text: 'Emulate OXD to ODK', toolTipText:
                            'Make extra effort to make sure the XPath formulas are ODK Compatible.\n ' +
                                    'e.g XPath formulas are parsed and references to multi-select are corrected using the right odk syntax')
                    chkAutoAddInstanceId = checkBoxMenuItem(text: 'Automatically Add Meta InstanceID', selected: true)


                    separator()
                    menuItem(text: '  OpenXData:', font: new Font(Font.SANS_SERIF, Font.BOLD, 12))
                    separator()
                    chkGenerateLayout = checkBoxMenuItem(text: 'Generate Layout')
                    chkSerializeExtraAttributesToComment = checkBoxMenuItem(text: 'Store Extra Attributes in Comment', selected: true,
                            toolTipText: 'This is a compatibility hack when converting openxdata xform to odk where extra bind and layout attributes are stored in the openxdata comment section')
                }

                menu(text: 'Help') {

                    menuItem(text: 'Select Example Below', font: new Font(Font.SANS_SERIF, Font.BOLD, 12))
                    separator()

                    Resources.EXAMPLES.each { k, v ->
                        menuItem(text: k, icon: ICON_FORM, actionPerformed: { formLoader(v) })
                    }

                    separator()
                    menuItem(text: 'About', actionPerformed: { showAbout() })
                }
            }
        }

        def _toolBar = {
            s.panel(constraints: BorderLayout.NORTH) {

                flowLayout(alignment: FlowLayout.LEFT, hgap: 1)

                btnGenerateXML = button(text: "Export OXD Study")
                btnShowXml = button(text: "Show XML OXD")
                btnShowOdkXml = button(text: "Show XML ODK")
                btnPreviewXml = button(text: "Preview in Enketo")
                chkNumberLabels = checkBox(text: 'Number Labels', selected: true)
                chkODKValidate = checkBox(text: 'ODK Validate', selected: true)
                btnIncreaseFont = button(text: 'A+', selected: true, toolTipText: 'Increase Font')
                btnDecreaseFont = button(text: 'A-', selected: true, toolTipText: 'Decrease Font')

            }
        }

        def _tree = {
            s.panel(preferredSize: [250, -1]) {

                borderLayout()

                panel(constraints: BorderLayout.NORTH) {
                    flowLayout(alignment: FlowLayout.LEFT)
                    btnRefreshTree = button(icon: ICON_REFRESH)
                    chkAutoUpdateTree = checkBox(text: 'Auto Refresh', selected: true)
                }

                scrollPane() {
                    studyTreeBuilder = widget(new StudyTreeBuilder()) as StudyTreeBuilder
                }
            }
        }

        def _bottomBar = {
            s.panel(constraints: BorderLayout.SOUTH) {

                flowLayout(alignment: FlowLayout.RIGHT)

                label('Line:')
                lblCaret = label('78:14(1314)')
            }
        }

        def _textArea = {
            s.splitPane(orientation: JSplitPane.VERTICAL_SPLIT, resizeWeight: 0.70) {


                scrollPane(constraints: 'top') {
                    txtMarkUp = editorPane()
                    txtMarkUp.setContentType("text/xform")
                }

                s.scrollPane(constraints: 'bottom') {
                    txtConsole = s.widget(new JTextWrapPane(
                            styledDocument: new DefaultStyledDocument(),
                            font: new Font(Font.MONOSPACED, Font.PLAIN, 12),
                            background: new Color(255, 255, 218))
                    ) as JTextWrapPane
                }.getViewport().setBackground(new Color(255, 255, 218))
//Set the background of the scrollPane same as textPane


            }
        }

        frame = s.frame(title: 'OXD-Markup', defaultCloseOperation: DO_NOTHING_ON_CLOSE,
                size: [800, 600], show: true, locationRelativeTo: null) {

            lookAndFeel('system')

            _menuBar()
            _toolBar()

            splitPane(constraints: BorderLayout.CENTER) {
                _tree()
                _textArea()
            }

            _bottomBar()
        }


        MessageConsole con = new MessageConsole(txtConsole);
        con.with {
            //setMessageLines(2000)
            redirectOut Color.black, System.out
            redirectErr Color.red, System.err
            info = createStream(Color.blue)
        }
        new CaretMonitor(txtMarkUp, lblCaret)
    }

    private void showAbout() {
        s.dialog(owner: frame, locationRelativeTo: frame, title: 'About OxdMarkup',
                visible: true, defaultCloseOperation: DISPOSE_ON_CLOSE, size: [200, 100],
                modal: true) {
            panel {
                gridLayout(columns: 1, rows: 3)
                label('Developed By')
                label('web:   http://www.omnitech.co.ug')
                label('Email: rk@omnitech.co.ug')

            }

        }
    }


    def renderHistory(java.util.List<String> history, Closure doLoadFile) {
        menuRecent.clear()
        def s = new SwingBuilder()

        def onMenuSelected = { ActionEvent e ->
            def menuItem = e.source as JMenuItem
            doLoadFile(menuItem.text)
        }

        invokeLater {
            history.collect { item ->
                menuRecent.add(s.menuItem(text: item, actionPerformed: onMenuSelected))
            }
        }
    }

    void setTitle(String s) {
        frame.title = s
    }

    void setFontSize(int fontSize) {
        def kit = txtMarkUp.getEditorKit() as XFormMarkupSyntaxKit

        invokeLater {
            kit.deinstallComponent(txtMarkUp, LineNumbersRuler.name)
            def f = txtMarkUp.getFont()
            txtMarkUp.setFont(new Font(f.name, f.style, fontSize))
            kit.installComponent(txtMarkUp, LineNumbersRuler.name)

            Map<JEditorPane, java.util.List<SyntaxComponent>> comps = ReflectionUtils.getValue(kit, kit.class.superclass, true, 'editorComponents')
            def ruler = comps.get(txtMarkUp).find { it instanceof LineNumbersRuler } as LineNumbersRuler
            ruler?.setMinimumDisplayDigits(2)
        }
    }

    void increaseFont() {
        def size = txtMarkUp.font.getSize()
        studyTreeBuilder.increaseFont()
        setFontSize(size + 1)
    }

    void decreaseFont() {
        def size = txtMarkUp.font.getSize()
        studyTreeBuilder.decreaseFont()
        setFontSize(size - 1)
    }

    static protected ImageIcon createImageIcon(String path,
                                               String description) {
        URL imgURL = getClass().getResource(path)
        if (imgURL != null) {
            return new ImageIcon(imgURL, description)
        } else {
            System.err.println("Couldn't find file: " + path)
            return null
        }
    }

    JButton btnGenerateXML
    JButton btnShowXml
    JButton btnShowOdkXml
    JButton btnRefreshTree
    JButton btnPreviewXml
    JButton btnIncreaseFont
    JButton btnDecreaseFont

    JCheckBoxMenuItem chkGenerateLayout
    JCheckBoxMenuItem chkNumberBindings
    JCheckBox chkNumberLabels
    JCheckBox chkODKValidate
    JCheckBoxMenuItem chkEmulateOXDConversion
    JCheckBoxMenuItem chkAutoAddInstanceId
    JCheckBox chkAutoUpdateTree
    JCheckBoxMenuItem chkUseXMLValidation
    JCheckBoxMenuItem chkEnsureUniqueIdentifier
    JCheckBoxMenuItem chkEnableAutoSave
    JCheckBoxMenuItem chkSerializeExtraAttributesToComment
    JLabel lblCaret

    //menus
    JMenuItem menuImport
    JMenuItem menuAlign
    JMenuItem menuNew
    JMenuItem menuRecent
    JMenuItem menuOpen
    JMenuItem menuSave
    JMenuItem menuEnableODKMode
    JSplitPane spltMainPane

    //Text
    JEditorPane txtMarkUp
    JTextPane txtConsole

    StudyTreeBuilder studyTreeBuilder

    JFrame frame

    static main(args) {
        new MainUI()
    }


}

