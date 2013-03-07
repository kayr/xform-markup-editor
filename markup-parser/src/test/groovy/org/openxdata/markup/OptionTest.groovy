package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/28/13
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
class OptionTest extends GroovyTestCase {
    void testSetOption() {
        Option option = new Option('$hello Hello world')

        assertEquals option.bind , 'hello'
        assertEquals  "Hello world",option.option


        option.setOption("Hellow world")

        assertEquals Util.getBindName(option.option) , option.bind


    }
}
