package org.openxdata.markup.deserializer

import org.openxdata.markup.Form
import org.openxdata.markup.Study

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
            study.addForms(forms)
        }
        return study
    }

    List<Form> toForms(Node formXML) {
        def forms = []
        formXML.version.eachWithIndex {vNode, idx ->
            forms << toForm(vNode.xform.text())
        }
        return forms
    }

    Form toForm(String text){
        return new FormDeserializer(xml: text).parse()
    }

}
