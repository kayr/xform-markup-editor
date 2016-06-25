package org.openxdata.markup.deserializer

import org.openxdata.markup.ParserUtils

import static org.openxdata.markup.deserializer.MarkupDeserializer.createAST

/**
 * Created by kay on 5/26/2016.
 */
class MarkupDeserializerTest extends GroovyTestCase {


    void testParse() {

        def erraticForm = """### fkfkf

## fjfjf
uu
   '''sdsdsd
ONew

sdsdsd''"""
        def ser = new MarkupDeserializer(erraticForm)


        shouldFail(RuntimeException) {
            ser.parse()
        }

    }

    void testPrint(){
        def p = '''
### djsjdj

## jdjsjd

@id oldpageid
#> old page

q1

@id groupid
group{ new page
    q2
}


ksd kskd
'''



        ParserUtils.printTree(createAST(p))
    }
}
