package org.openxdata.markup.deserializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.MultiSelectQuestion
import org.openxdata.markup.SingleSelectQuestion

import static org.openxdata.markup.Form.extractQuestions

/**
 * Created by kay on 6/7/14.
 */
class FormDeserializerTest extends GroovyTestCase {


    void testToForm() {
        def form = new FormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.name == 'Snv Form'
        assert form.id == 'snv_study_snv_form_v1'
    }

    void testAddPages() {
        def form = new FormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.pages.size() == 1
        assert form.pages[0].name == 'Page1'
    }

    void testNumberOfQuestions() {
        def form = new FormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.allQuestions.size() == 10
        assert extractQuestions(form).size() == 10
    }

    void testSingleSelectOption() {
        def form = new FormDeserializer(xml: Fixtures.expectedXForm).parse()

        def question = form.allQuestions.find { it.binding == 'what_is_sex' }
        assert question instanceof SingleSelectQuestion

        def options = question.options
        assert options.size() == 2
        assert options.any { it.bind == 'male' && it.text == 'Male'}
        assert options.any { it.bind == 'female' && it.text == 'female'}
    }

    void testMultipleSelectOption() {
        def form = new FormDeserializer(xml: Fixtures.expectedXForm).parse()

        def question = form.allQuestions.find { it.binding == 'select_your_diseases' }
        assert question instanceof MultiSelectQuestion

        def options = question.options
        assert options.size() == 3
        assert options.any { it.bind == 'aids' && it.text == 'AIDS' }
        assert options.any { it.bind == 'tb' && it.text == 'TB' }
        assert options.any { it.bind == 'whooping_cough' && it.text == 'Whooping cough' }
    }


}
