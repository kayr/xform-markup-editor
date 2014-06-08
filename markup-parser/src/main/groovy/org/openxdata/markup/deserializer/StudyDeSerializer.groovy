package org.openxdata.markup.deserializer

import org.openxdata.markup.Form
import org.openxdata.markup.Study
import org.openxdata.markup.Page

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 3/11/13
 * Time: 7:45 PM
 * To change this template use File | Settings | File Templates.
 */
class StudyDeSerializer {

    def parser = new XmlParser()

    Study toStudy(String studyXML) {

        def study = new Study()

        def xml = parser.parseText(studyXML)
        study.name = xml.@name
        xml.form.each {
            def forms = toForms(it)
            study.forms.addAll(forms)
        }
        return study
    }

    List<Form> toForms(Node formXML) {
        def rootFormName = formXML.@name

        def forms = []

        formXML.version.eachWithIndex {vNode, idx ->
            def form = new Form()

            if (idx == 0) {
                form.name = rootFormName
            } else {
                form.name = "$rootFormName $vNode.name"
            }
            toForm(vNode.xform.text())
            forms << form
        }
        return forms
    }

    Form toForm(String text){

//        def form = XForm.fromXform2FormDef(new StringReader(text))

        form.pages.each {
            def page = new Page()

        }

        return null

    }

}
