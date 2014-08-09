package org.openxdata.markup.ui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 * Created by kay on 7/11/14.
 */
class XFormsUI {

    def s = new SwingBuilder()
    JFrame parent
    JTabbedPane tabs
    List<JEditorPane> editors = []

    XFormsUI(JFrame parent) {
        this.parent = parent
        init()
    }

    private def init() {
       def frame = s.frame(size: [525, 352], visible: true, title: 'XForm XML',
                defaultCloseOperation: WindowConstants.DISPOSE_ON_CLOSE,
                alwaysOnTop: false, locationRelativeTo: parent) {

            tabs = tabbedPane()
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                cleanUp()
            }
        })

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
        new XFormsUI(new JFrame())
    }

}
