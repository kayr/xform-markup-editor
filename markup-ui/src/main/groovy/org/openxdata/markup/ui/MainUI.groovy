package org.openxdata.markup.ui

import groovy.swing.SwingBuilder
import jsyntaxpane.DefaultSyntaxKit
import jsyntaxpane.actions.CaretMonitor

import javax.swing.*
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
    static def ICON_OPEN = getIcon('Tree.openIcon')
    static def ICON_SAVE = getIcon('FileView.floppyDriveIcon')
    static def ICON_NEW = getIcon('FileView.fileIcon')
    static def ICON_IMPORT = getIcon('FileChooser.upFolderIcon')
    static def ICON_FORM = getIcon('FileChooser.detailsViewIcon')
    static def ICON_REFRESH = createImageIcon('/refresh.png', 'Refresh')

    Closure formLoader
    SwingBuilder s

    MainUI() {
        DefaultSyntaxKit.registerContentType("text/xform", "org.openxdata.markup.ui.XFormMarkupSyntaxKit");
        initUi()

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

                    menuItem(text: '  General:', font: new Font(Font.SANS_SERIF, Font.BOLD, 12))
                    separator()
                    chkNumberBindings = checkBoxMenuItem(text: 'Number IDs')
                    chkUseXMLValidation = checkBoxMenuItem(text: 'Allow Invalid OXD IDs(OpenXData Lower Case Ids Only)')
                    chkEnsureUniqueIdentifier = checkBoxMenuItem(text: 'Ensure Unique Identifier Question Exists', selected: true)


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
                chkNumberLabels = checkBox(text: 'Number Labels')

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
            s.splitPane(orientation: JSplitPane.VERTICAL_SPLIT, resizeWeight: 0.85) {

                scrollPane(constraints: 'top') {
                    txtMarkUp = editorPane()
                    txtMarkUp.setContentType("text/xform");
                }

                scrollPane(constraints: 'bottom') {
                    txtConsole = textArea()
                }
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
            setMessageLines(300)
            redirectOut Color.black, System.out
            redirectErr Color.black, System.err
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

    static protected ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    JButton btnGenerateXML
    JButton btnShowXml
    JButton btnShowOdkXml
    JButton btnRefreshTree
    JButton btnPreviewXml

    JCheckBoxMenuItem chkGenerateLayout
    JCheckBoxMenuItem chkNumberBindings
    JCheckBox chkNumberLabels
    JCheckBoxMenuItem chkEmulateOXDConversion
    JCheckBoxMenuItem chkAutoAddInstanceId
    JCheckBox chkAutoUpdateTree
    JCheckBoxMenuItem chkUseXMLValidation
    JCheckBoxMenuItem chkEnsureUniqueIdentifier
    JCheckBoxMenuItem chkSerializeExtraAttributesToComment
    JLabel lblCaret

    //menus
    JMenuItem menuImport
    JMenuItem menuAlign
    JMenuItem menuNew
    JMenuItem menuRecent
    JMenuItem menuOpen
    JMenuItem menuSave
    JSplitPane spltMainPane

    //Text
    JEditorPane txtMarkUp
    JTextArea txtConsole

    StudyTreeBuilder studyTreeBuilder

    JFrame frame

    static main(args) {
        new MainUI()
    }


}
