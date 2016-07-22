package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.exception.ValidationException

import static org.openxdata.markup.ParserUtils.printTree
import static org.openxdata.markup.XPathUtil.createAST

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

        assert xpathUtil.tree.findAllDeep { true }.size() == 38

    }

    void testVariableResolving() {

        def simpleForm = '''
### study

## form

one

two

repeat{ repeat
  @showif $jfdjf = true
  rone
}

'''


        shouldFail(ValidationException) {
            new MarkupDeserializer(simpleForm).study().forms.first()
        }


        Study study = new MarkupDeserializer(simpleForm, false).study()
        assert study.forms.size() > 0

    }

    void testManyTest() {
        printTree(createAST('''/abs/olute/path[$etc = -2]/sdsd'''))
    }
}
