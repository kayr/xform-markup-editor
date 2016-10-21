package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.exception.ValidationException

import static org.openxdata.markup.ParserUtils.*

/**
 * Created by kay on 6/18/14.
 */
class XPathUtilTest extends GroovyTestCase {
    void testShorten() {

    }

    void testEmitString() {

        def xpathUtil = new XPathUtil("/some/path = 'male'  and /other/path != 'female'")

        def g = findAllDeepSelf(xpathUtil.tree) { CommonTree ctree ->
            ctree.token.type == XPathParser.ABSPATH

        }

        assert ['/some/path', '/some/path'].every { path ->
            g.any { emitTailString(it) == path }
        }
    }

    void testCollect() {

        String xpath = "some/path = 'male'  and /other/path != 'female'"
        def xpathUtil = new XPathUtil(xpath)

        def g = findResultsDeep(xpathUtil.tree) { CommonTree ctree ->
            if (XPathUtil.isPath(ctree)) {
                return emitTailString(ctree)
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

        def g = findResultsDeep(xpathUtil.tree) { CommonTree ctree ->
            if (XPathUtil.isPath(ctree)) {
                return emitTailString(ctree)
            }
        }

        assert ['/study_form_v1/weight', '/study_form_v1/heightcm'].every { path ->
            g.any { it == path }
        }

    }

    void testFindAllDeep() {

        String xpath = "/study_form_v1/weight div ((/study_form_v1/heightcm div 100.0)*(/study_form_v1/heightcm div 100.0))"
        def xpathUtil = new XPathUtil(xpath)

        assert findAllDeepSelf(xpathUtil.tree) { true }.size() == 38

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

    void testDoubleDotDoesNotThrowException() {
        def markup = '''
            ### s

            ## s

            question

            @id g1
            group{
                @showif .. = true
                some qn
            }
            '''

        try {
            ConversionHelper.markup2Form(markup)
        } catch (Exception x) {
            x.printStackTrace()
            fail("Double dot should not throw exception")
        }
    }

    void testDoubleDotAgain() {
        def markup = '''
            ### s

            ## s

            question

            @id g1
            group{
                @showif ../fd/question[@attr = 'df'] = true
                some qn
            }
            '''

        try {
            ConversionHelper.markup2Form(markup)
        } catch (Exception x) {
            x.printStackTrace()
            fail("Double dot should not throw exception")
        }
    }
}
