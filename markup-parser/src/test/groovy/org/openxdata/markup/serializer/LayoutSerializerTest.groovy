package org.openxdata.markup.serializer;

import junit.framework.TestCase;
import org.openxdata.markup.Fixtures;
import org.openxdata.markup.Util;

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/4/13
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutSerializerTest extends TestCase {

    public void testGenerateLayout() throws Exception {

        LayoutSerializer ser = new LayoutSerializer()

        def form = Util.createParser(Fixtures.normalPurcform2).study().forms[0]

        def xml = ser.generateLayout(form)



        println xml

//        XFormSerializer serializer = new XFormSerializer()
//        println serializer.toXForm(form)
    }
}
