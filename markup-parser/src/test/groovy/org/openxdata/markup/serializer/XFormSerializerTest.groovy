package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study
import org.openxdata.markup.Util

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializerTest extends GroovyTestCase {

    private Study study
    XFormSerializer serializer = new XFormSerializer();

    void setUp() {

        def parser = Util.createParser(Fixtures.normalPurcform)
        study = parser.study()

    }

    void testBuildStudy() {

    }

    void testToXForm() {
        def form = study.forms[0]

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.expectedXForm, xml
    }

    void testTodStudy() {
        def studyXml = serializer.toStudyXml(study)

        assertEquals Fixtures.snvStudyXML, studyXml
    }

    void testToXformWithDataTypes() {
        def parser = Util.createParser(Fixtures.formWithAttribs)

        def xml = serializer.toXForm(parser.study().forms[0])
        assertEquals Fixtures.xformWithAttribsXML, xml
    }

    void testToXFormWithSkipLogic() {
        def parser = Util.createParser(Fixtures.formWithSkipLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithSkipLogicXML, xml
    }

    void testToXFormWithValidationLogic() {
        def parser = Util.createParser(Fixtures.formWithValidationLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xfromWithValidationLogicXML, xml
    }

    void testRepeatWithAttributes() {
        def parser = Util.createParser(Fixtures.formRepeatWithAttributesOnRepeats)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithRepeatAttributesXML, xml

    }

}
