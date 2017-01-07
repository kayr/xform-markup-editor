package org.openxdata.markup.deserializer

import org.openxdata.markup.TestUtils
import org.openxdata.markup.serializer.MarkUpSerializer

/**
 * Created by kay on 1/1/2017.
 */
class ODKDeSerializerTest extends GroovyTestCase {
    void testParsingTide() {
        testConversion(
                'ODKDeserializer/TIDE_farmassessment_pilot_final_v1.xml',
                'ODKDeserializer/TIDE_farmassessment_pilot_final_v1.xfm')
    }

    void testParsingHiddenGroupInVirtualGroup() {
        testConversion(
                'ODKDeserializer/hidden_group_in_virtual_group.xml',
                'ODKDeserializer/hidden_group_in_virtual_group.xfm')
    }

    static void testConversion(String pathToXml, String pathToXfm, boolean printOutput = false) {
        def f = new ODKDeSerializer(TestUtils.loadResourceText(pathToXml)).parse()
        def unTrimmedMarkup = MarkUpSerializer.toFormMarkUp(f)
        def markup = TestUtils.trimAllLines(unTrimmedMarkup)

        def expected = TestUtils.loadResourceText(pathToXfm)

        if (printOutput) {
            println(unTrimmedMarkup)
        }

        assertEquals TestUtils.trimAllLines(expected), markup
    }
}
