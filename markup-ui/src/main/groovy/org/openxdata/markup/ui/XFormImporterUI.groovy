package org.openxdata.markup.ui

import groovy.swing.SwingBuilder

import javax.swing.*
import javax.swing.filechooser.FileFilter as SFilter
import java.awt.*

import static javax.swing.SwingUtilities.invokeLater

/**
 * Created by kay on 7/11/14.
 */
class XFormImporterUI {

    private XFormImporterPresenter presenter

    private def filter
    private SwingBuilder s = new SwingBuilder()
    private JTextArea txtXml
    private JFrame frame

    XFormImporterUI(XFormImporterPresenter presenter) {
        this.presenter = presenter
        init()

        filter = [
                accept: { File file -> file.name.endsWith('.xml') || file.isDirectory() },
                getDescription: { "XML Files" }
        ] as SFilter

    }

    private void init() {
        s = new SwingBuilder()

        s.edt {}
        def _searchPanel = {
            s.panel(constraints: BorderLayout.NORTH) {
                button(text: "Browse File",
                        actionPerformed: {
                            loadFile()
                        }
                )
                button(text: "Process XML",
                        actionPerformed: {
                            Thread.start { presenter.processXml() }
                        }
                )
            }
        }

        def _resultsPanel = {
            s.scrollPane(constraints: BorderLayout.CENTER) {
                txtXml = textArea()
            }
        }

        frame = s.frame(title: "OXD MarkUp: Import XForm",
                defaultCloseOperation: JFrame.HIDE_ON_CLOSE,
                size: [500, 400],
                show: false) {
            _searchPanel()
            _resultsPanel()
        }
    }

    private loadFile() {

        def fc = s.fileChooser(fileFilter: filter)

        def option = fc.showOpenDialog(frame)

        if (option == JFileChooser.APPROVE_OPTION) {
            def file = fc.getSelectedFile()
            presenter.load(file)
        }
    }

    def setXML(String xml) {
        invokeLater {
            txtXml.setText(xml)
        }
    }

    String getXML() {
        txtXml.text
    }


    Object show(Point location) {
        invokeLater {
            frame.setLocation(location)
            frame.setVisible(true)
        }
    }


    Object hide() {
        invokeLater {
            frame.setVisible(false)
        }
    }
}
