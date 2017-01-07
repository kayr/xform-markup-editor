package org.openxdata.markup.ui

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.deserializer.StudyDeSerializer
import org.openxdata.markup.deserializer.XFormDeserializer
import org.openxdata.markup.serializer.MarkUpSerializer
/**
 * Created by kay on 7/11/14.
 */
class XFormImporterPresenter {

    private XFormImporterUI ui
    private MainPresenter main

    XFormImporterPresenter() {
        ui = new XFormImporterUI(this)
    }

    XFormImporterPresenter(MainPresenter main) {
        this()
        this.main = main
    }

    def load(File file) {
        ui.setXML(IOHelper.loadText(file))
    }

    def processXml() {
        def xml = ui.XML
        def dom = new XmlSlurper(false, false).parseText(xml)

        String markup

        if (isForStudy(dom)) {
            StudyDeSerializer sd = new StudyDeSerializer()
            def study = sd.toStudy(xml)
            markup = MarkUpSerializer.toStudyMarkup(study)
        } else if (isForOdk(dom)) {
//            markup = MarkUpSerializer.toFormMarkUp(ConversionHelper.odk2Form(xml))
            markup = Converter.from(FORMAT.ODK).to(FORMAT.MARKUP).convert(xml)
            main?.enableODKMode()
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

    static boolean isForOdk(dom) {
        return dom.name().endsWith('html')
    }


    static main(args) {
        new XFormImporterPresenter(null)
    }
}
