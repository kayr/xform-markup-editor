package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.deserializer.DeSerializerFixtures
import org.openxdata.markup.deserializer.MarkupDeserializer

import static org.openxdata.markup.Fixtures.*
/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializerTest extends GroovyTestCase {

    def serializer = new XFormSerializer()

    void testTopMarkUp() {
        setFormDirectory()
        testRoundTrip(formWithAbsoluteId)
        testRoundTrip(multipleForms)
        testRoundTrip(formWithEndtime)
        testRoundTrip(oxdSampleForm)
        testRoundTrip(formUsingRelativeBinds)
        testRoundTrip(formWithCSVImport)
        testRoundTrip(formWithDynamicInstanceReferences)
        testRoundTrip(formWithValidationOnInnerRepeat)
        testRoundTrip(skipLogicInRepeat)
        testRoundTrip(skipLogicInRepeat)
        testRoundTrip(formRepeatWithAttributesOnRepeats)
        testRoundTrip(formWithMultiplePage)
        testRoundTrip(formWithSkipLogic)
        testRoundTrip(formWithActionAttributes)
        testRoundTrip(formWithValidationLogic)
        testRoundTrip(normalPurcform)
        testRoundTrip(formWithId)
        testRoundTrip(normalPurcform2)
        testRoundTrip(formWithDollarInString)
        testRoundTrip(DeSerializerFixtures.dynFormWithQuotes.markUp)
        testRoundTrip(DeSerializerFixtures.nestedGroups.markUp)
        testRoundTrip(Fixtures.formWithLayoutAndBindAttributes)
    }

    void testRoundTrip(String form) {
        def studyObj1 = new MarkupDeserializer(form).study()

        // get study to xml 1
        def studyXML1 = serializer.toStudyXml(studyObj1)

        def form2 = MarkUpSerializer.toStudyMarkup(studyObj1)

        // get study xml 2
        def studyObj2 = new MarkupDeserializer(form2).study()
        def studyXML2 = serializer.toStudyXml(studyObj2)

        assertEquals studyXML1, studyXML2
    }

}
