package org.openxdata.markup.ui

import groovy.swing.SwingBuilder

import javax.swing.*

/**
 * Created by kay on 7/11/14.
 */
class XFormsUI {

    def s = new SwingBuilder()
    JFrame parent
    JTabbedPane tabs

    XFormsUI(JFrame parent) {
        this.parent = parent
        init()
    }

    private def init() {
        s.dialog(size: [525, 352], visible: true, title: 'XForm XML',
                defaultCloseOperation: WindowConstants.DISPOSE_ON_CLOSE,
                alwaysOnTop: true, locationRelativeTo: parent) {
            tabs = tabbedPane()
        }

    }

    def addTab(String name, String xml) {
        s.edt {
            def pane = scrollPane() {
                def editor = editorPane(name: name)
                editor.contentType = 'text/xml'
                editor.read(new StringReader(xml), 'text/xml')
            }
            tabs.addTab(name, pane)
        }
    }

    static main(args) {
        new XFormsUI(new JFrame())
    }

}
