package org.openxdata.markup.util

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT

import java.util.regex.Pattern

/**
 * Created by user on 7/3/2017.
 */
class TextUtilsTest extends GroovyTestCase {
    void testParseText() {

        assertTokens("HHHhsns  {{44343}} kdkkskd {{434 k4k4k jdjsdfv {{}} sdjjsd }} jsdjds",
                [
                        0: "HHHhsns  ",
                        1: "{{44343}}",
                        2: " kdkkskd ",
                        3: "{{434 k4k4k jdjsdfv {{}}",
                        4: " sdjjsd }} jsdjds",
                ]
        )


        assertTokens("{{44343}}", [0: "{{44343}}"])

        assertTokens("{44343}}", [0: "{44343}}"])

        assertTokens("", [0: ""])

        assertTokens(null, [:])

        assertTokens("{{sd}}{{sd}}", [0: "{{sd}}", 1: "{{sd}}"])


    }

    void testParseTextOXD() {

        assertTokens('HHHhsns  ${44343}$ kdkkskd ${434 k4k4k jdjsdfv ${}$ sdjjsd }$ jsdjds',
                [
                        0: 'HHHhsns  ',
                        1: '${44343}$',
                        2: ' kdkkskd ',
                        3: '${434 k4k4k jdjsdfv ${}$',
                        4: ' sdjjsd }$ jsdjds',
                ], TextParser.OXD_PATTERN
        )


        assertTokens('${44343}$', [0: '${44343}$'], TextParser.OXD_PATTERN)

        assertTokens('{44343}$', [0: '{44343}$'], TextParser.OXD_PATTERN)

        assertTokens('', [0: ''], TextParser.OXD_PATTERN)

        assertTokens(null, [:], TextParser.OXD_PATTERN)

        assertTokens('${sd}$${sd}$', [0: '${sd}$', 1: '${sd}$'], TextParser.OXD_PATTERN)


    }

    static def assertTokens(String sorceText, Map<Integer, String> tokenText, Pattern p = TextParser.PATTERN) {
        def tokens = TextParser.parseText(sorceText, p, '$', '%')
        assert tokens.size() == tokenText.size()

        tokenText.each { position, text ->

            assert tokens[position].text == text

        }


    }


    void testOut() {
        def f = '''##f

            @hint {{$name}}
            Name
            
            Sex of {{$name}}'''

        def odk = Converter.to(FORMAT.ODK, String).from(FORMAT.MARKUP).convert(f)

        assertEquals '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>f</h:title>
    <model>
      <instance>
        <f_v1 id="0" name="f">
          <name />
          <sex_of_name />
        </f_v1>
      </instance>
      <bind id="name" nodeset="/f_v1/name" type="string" />
      <bind id="sex_of_name" nodeset="/f_v1/sex_of_name" type="string" />
    </model>
  </h:head>
  <h:body>
    <input ref="/f_v1/name">
      <label>Name</label>
      <hint><output value="/f_v1/name" /></hint>
    </input>
    <input ref="/f_v1/sex_of_name">
      <label>Sex of <output value="/f_v1/name" /></label>
    </input>
  </h:body>
</h:html>''', odk

        def oxd = Converter.to(FORMAT.OXD, String).from(FORMAT.MARKUP).convert(f)

        assert oxd == '''<xforms>
  <model>
    <instance id="f_v1">
      <f_v1 id="0" name="f" formKey="f_v1">
        <name />
        <sex_of_name />
      </f_v1>
    </instance>
    <bind id="name" nodeset="/f_v1/name" type="xsd:string" />
    <bind id="sex_of_name" nodeset="/f_v1/sex_of_name" type="xsd:string" />
  </model>
  <group id="1" isSynthetic="true">
    <label>Page1</label>
    <input bind="name">
      <label>Name</label>
      <hint>${/f_v1/name}$</hint>
    </input>
    <input bind="sex_of_name">
      <label>Sex of ${/f_v1/name}$</label>
    </input>
  </group>
</xforms>'''


        def markup = Converter.to(FORMAT.MARKUP, String).from(FORMAT.OXD).convert(oxd)

        assertEquals '''@id f_v1
## f


@comment {{$name}}
Name


Sex of {{$name}}''', markup

        //JUST BEING SURE HERE
        assert Converter.to(FORMAT.OXD, String).from(FORMAT.MARKUP).convert(markup) == oxd
        assert Converter.to(FORMAT.ODK, String).from(FORMAT.MARKUP).convert(markup) == odk


    }
}
