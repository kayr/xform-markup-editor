package org.openxdata.markup.ui

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.Form
import org.openxdata.markup.deserializer.StudyDeSerializer
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
            study.forms.each { makeUniqueIdAbsoluteMayBe(it) }
            markup = MarkUpSerializer.toStudyMarkup(study)
        } else if (isForOdk(dom)) {
            def form = Converter.toFormFrom(FORMAT.ODK, xml)
            makeUniqueIdAbsoluteMayBe(form)
            markup = Converter.fromFormTo(FORMAT.MARKUP, form)
            main?.enableODKMode()
        } else {
            def form = Converter.toFormFrom(FORMAT.OXD, xml)
            makeUniqueIdAbsoluteMayBe(form)
            markup = "### $form.name\n${MarkUpSerializer.toFormMarkUp(form)}\n"
        }
        main?.loadWithConfirmation(markup)

        ui.hide()
    }

    static makeUniqueIdAbsoluteMayBe(Form form) {
        form['unique_id']?.setHasAbsoluteId(true)
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
