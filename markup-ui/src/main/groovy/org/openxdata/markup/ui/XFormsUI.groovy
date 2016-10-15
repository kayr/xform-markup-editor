package org.openxdata.markup.ui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.List

/**
 * Created by kay on 7/11/14.
 */
class XFormsUI {

    def s = new SwingBuilder()
    JFrame parent, frame

    JTabbedPane tabs
    List<JEditorPane> editors = []
    JButton btnSave


    XFormsUI(JFrame parent) {
        this.parent = parent
        init()
    }

    private def init() {
        frame = s.frame(size: [525, 352], visible: true, title: 'XForm XML',
                defaultCloseOperation: WindowConstants.DISPOSE_ON_CLOSE,
                alwaysOnTop: false, locationRelativeTo: parent) {

            panel(constraints: BorderLayout.NORTH) {
                btnSave = button(text: 'Save To File', icon: MainUI.ICON_SAVE)
            }

            tabs = tabbedPane(constraints: BorderLayout.CENTER)

        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                cleanUp()
            }
        })

    }


    JEditorPane getSelectedView() {
        def cmp = tabs.getComponentAt(tabs.getSelectedIndex()) as JScrollPane
        def component = cmp.getViewport().getComponent(0) as JEditorPane
        return component
    }

    def addTab(String name, String xml) {
        s.edt {
            def pane = scrollPane() {
                def editor = editorPane(name: name)
                editor.contentType = 'text/xml'
                editor.read(new StringReader(xml), 'text/xml')
                editors << editor
            }
            tabs.addTab(name, pane)
        }
    }

    def cleanUp() {
        editors.each {
            it.read(new StringReader(''), 'text/xml')
        }
        editors.clear()
    }

    static main(args) {

        def i = new XFormsUI(new JFrame())
        i.addTab('dsd', '<a/>')
    }

}
