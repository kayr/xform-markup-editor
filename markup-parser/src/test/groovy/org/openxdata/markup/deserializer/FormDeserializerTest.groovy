package org.openxdata.markup.deserializer

import org.openxdata.markup.Fixtures

/**
 * Created by kay on 6/7/14.
 */
class FormDeserializerTest extends GroovyTestCase {


    void testGetForm() {
        def ds = new FormDeserializer(xml: Fixtures.expectedXForm).parseXml()

        println ds.getFieldNames()

//        println ds.iterateFieldLabels()

    }

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
}
