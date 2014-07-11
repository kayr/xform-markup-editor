package org.openxdata.markup.ui

import org.openxdata.markup.deserializer.StudyDeSerializer
import org.openxdata.markup.deserializer.XFormDeserializer
import org.openxdata.markup.serializer.MarkUpSerializer

/**
 * Created by kay on 7/11/14.
 */
class XFormImporterPresenter {

    private XFormImporterUI ui
    private FormBinderPresenter main

    XFormImporterPresenter() {
        ui = new XFormImporterUI(this)
    }

    XFormImporterPresenter(FormBinderPresenter main) {
        this()
        this.main = main
    }

    def load(File file) {
        ui.setXML(file.text)
    }

    def processXml() {
        def xml = ui.XML
        def dom = new XmlSlurper(false, true).parseText(xml)

        String markup

        if (isForStudy(dom)) {
            StudyDeSerializer sd = new StudyDeSerializer()
            def study = sd.toStudy(xml)
            markup = MarkUpSerializer.toStudyMarkup(study)
        } else {
            XFormDeserializer ds = new XFormDeserializer(xml: xml, xForm: dom)
            def form = ds.toForm()
            markup = "### $form.name\n${MarkUpSerializer.toFormMarkUp(form)}\n"
        }
        main?.loadWithConfirmation(markup)
        ui.hide()
    }

    def show() {
        ui.show()
    }

    static boolean isForStudy(def dom) {
        String firstName = dom.name()
        return firstName.equalsIgnoreCase('study')
    }


    static main(args) {
        new XFormImporterPresenter(null)
    }
}
