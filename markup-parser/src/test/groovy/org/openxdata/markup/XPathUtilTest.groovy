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

        assert ['/some/path', '/some/path'].every { path ->
            g.any { it.emitTailString() == path }
        }
    }

    void testCollect() {

        def xpathUtil = new XPathUtil("some/path = 'male'  and /other/path != 'female'")

        def g = xpathUtil.findResults { CommonTree ctree ->
            if (ctree.token.type == XPathParser.ABSPATH || ctree.token.type == XPathParser.RELPATH) {
                return ctree.emitTailString()
            }

        }

        assert g.size() == 2
        assert ['some/path', '/other/path'].every { path ->
            g.any { it == path }
        }
    }
}
