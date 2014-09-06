package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree

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

        String xpath = "some/path = 'male'  and /other/path != 'female'"
        def xpathUtil = new XPathUtil(xpath)

        def g = xpathUtil.findResults { CommonTree ctree ->
            if (ctree.isPath()) {
                return ctree.emitTailString()
            }
        }

        assert g.size() == 2
        assert ['some/path', '/other/path'].every { path ->
            g.any { it == path }
        }
    }

    void testComplex() {

        String xpath = "/study_form_v1/weight div ((/study_form_v1/heightcm div 100.0)*(/study_form_v1/heightcm div 100.0))"
        def xpathUtil = new XPathUtil(xpath)

        def g = xpathUtil.findResults { CommonTree ctree ->
            if (ctree.isPath()) {
                return ctree.emitTailString()
            }
        }

        assert ['/study_form_v1/weight', '/study_form_v1/heightcm'].every { path ->
            g.any { it == path }
        }

    }

    void testFindAllDeep() {

        String xpath = "/study_form_v1/weight div ((/study_form_v1/heightcm div 100.0)*(/study_form_v1/heightcm div 100.0))"
        def xpathUtil = new XPathUtil(xpath)

        assert xpathUtil.tree.findAllDeep { true }.size() == 34

    }
}
