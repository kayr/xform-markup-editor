package org.openxdata.markup.serializer

import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.Fixtures
import org.openxdata.markup.deserializer.MarkupDeserializer
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/4/13
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
class LayoutSerializerTest extends XMLTestCase {

    //ignore
    void testGenerateLayout() throws Exception {

        LayoutSerializer ser = new LayoutSerializer()


        def form = new MarkupDeserializer(Fixtures.oxdSampleForm).study().forms[0]

        def xml = ser.generateLayout(form)

        //ignore layout xml
        assertXMLEqual(Fixtures.xmlOxdSampleForm, xml)
    }
}
