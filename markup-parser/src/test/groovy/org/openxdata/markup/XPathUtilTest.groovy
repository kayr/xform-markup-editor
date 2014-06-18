package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree
import org.openxdata.xpath.XPathParser

/**
 * Created by kay on 6/18/14.
 */
class XPathUtilTest extends GroovyTestCase {
    void testShorten() {

    }

    void testEmitString() {

        def xpathUtil = new XPathUtil("/some/path = 'male'  and /other/path != 'female'")

        def g = xpathUtil.findAll { CommonTree ctree ->
            ctree.token.type == XPathParser.ABSPATH

        }



       assert  ['/some/path', '/some/path'].every { path ->
            g.any { it.emitString() == path }
        }

        println xpathUtil.tree.emitString()



    }
}
