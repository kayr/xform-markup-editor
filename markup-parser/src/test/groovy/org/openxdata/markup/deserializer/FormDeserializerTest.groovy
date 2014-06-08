package org.openxdata.markup.deserializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Form

import static org.openxdata.markup.Form.extractQuestions

/**
 * Created by kay on 6/7/14.
 */
class FormDeserializerTest extends GroovyTestCase {


    void testToForm() {
        def ds = new FormDeserializer(xml: Fixtures.expectedXForm).parseXml()
        def form = ds.toForm()
        assert form.name == 'Snv Form'
        assert form.id == 'snv_study_snv_form_v1'
    }

    void testAddPages() {
        def ds = new FormDeserializer(xml: Fixtures.expectedXForm).parseXml()
        def form = ds.toForm()
        assert form.pages.size() == 1
        assert form.pages[0].name == 'Page1'
    }

    void testNumberOfQuestions() {
        def ds = new FormDeserializer(xml: Fixtures.expectedXForm).parseXml()
        def form = ds.toForm()
        assert form.allQuestions.size() == 10
        assert extractQuestions(form).size() == 10
    }
}
