package org.openxdata.markup.serializer

import org.openxdata.markup.Form
import org.openxdata.markup.Util

import static org.openxdata.markup.serializer.ODKFixtures.*

/**
 * Created by kay on 7/13/14.
 */
class ODKSerializerTest extends GroovyTestCase {
    def serializer = new ODKSerializer();

    void testReadonlyAndInvisibleIsConvertedToReadonly() {
        assertEquals formWithInvisible.xml, toODK(formWithInvisible.form)
    }

    void testReadonlyAndSkipLogicAreProcessedOk() {
        assertEquals formWithSkipLogicAndReadOnly.xml, toODK(formWithSkipLogicAndReadOnly.form)
    }

    void testStartTimeAnd() {
        assertEquals toODK(timeStamp.form, true), timeStamp.xml
    }

    void testFormWithRelativeValidation() {
        assertEquals formRelativeValidation.xml, toODK(formRelativeValidation.form)
    }

    void testOxdSampleForm() {
        assertEquals oxdSampleForm.xml, toODK(oxdSampleForm.form, true)
    }

    void testMultiSelectConversion() {
        assertEquals oxdSampleForm.xml, toODK(oxdSampleForm.form, true)
    }

    void testSkipActionsAndLogic() {
        serializer.numberBindings = true
        serializer.numberQuestions = true
        assertEquals formSkipLogicAndActions.xml, toODK(formSkipLogicAndActions.form, true)
    }

    void testRegex() {
        [
//                'length(.) = 4': '4', this is not supported in odk
                'length(.) = 4': null,
                'length(.) = "kdjjsd"': null,
                'length(.) = "kdjjsd" and 5 = 6': null,
                'length(.) = true() and 5 = 6': null,
                'length(.) = /form/field and 5 = 6': null,
                'length(.) = /ef/fr': '/ef/fr',
                'length(.) = ef/fr': '/ef/fr'
        ].each { reg ->
            def jrCount = ODKXpathUtil.getOXDJRCountOnRepeatValidation(reg.key)
            assertEquals reg.value, jrCount
        }
    }

    void testToODKMultiSelect() {
        Form form = toForm(multiSelectConversion.form)

        [
                '$subjects = \'calculus\' and ($ps != null or (3-4) = 9 or $subjects = \'grades\') and $subjects = \'biology\'':
                        "selected(/s_f_v1/subjects, 'calculus') and (/s_f_v1/ps != null or (3-4) = 9 or selected(/s_f_v1/subjects, 'grades')) and selected(/s_f_v1/subjects, 'biology')",
                '$subjects = \'calculus\' and $subjects != \'biology\'':
                        "selected(/s_f_v1/subjects, 'calculus') and not(selected(/s_f_v1/subjects, 'biology'))",
                '\'calculus\' = $subjects and $subjects != \'biology\'':
                        "selected(/s_f_v1/subjects, 'calculus') and not(selected(/s_f_v1/subjects, 'biology'))",
                '$subjects = \'calculus\' and $subjects != \'biology,calculus,math\'':
                        "selected(/s_f_v1/subjects, 'calculus') and not(selected(/s_f_v1/subjects, 'biology') or selected(/s_f_v1/subjects, 'calculus') or selected(/s_f_v1/subjects, 'math'))",
                '$ps = \'calculus\' and $subjects > \'biology\'':
                        "/s_f_v1/ps = 'calculus' and /s_f_v1/subjects > 'biology'",
                '$subjects = calc() and $ps = true':
                        'selected(/s_f_v1/subjects, calc()) and /s_f_v1/ps = true',
                '$subjects = calc() and ($subjects = "calculus" or $subjects != "calculus" and ($subjects != "calculus"))and $ps = true and $subjects != "calculus"':
                        "selected(/s_f_v1/subjects, calc()) and (selected(/s_f_v1/subjects, 'calculus') or not(selected(/s_f_v1/subjects, 'calculus')) and (not(selected(/s_f_v1/subjects, 'calculus'))))and /s_f_v1/ps = true and not(selected(/s_f_v1/subjects, 'calculus'))",
                '$subjects = $subjects': 'selected(/s_f_v1/subjects, /s_f_v1/subjects)'   ,
//                '''$subjects = $subjects and $subjects = concat( if($subjects = 'math' and $c = true and $c = true ,'true','false'))''': 'selected(/s_f_v1/subjects, /s_f_v1/subjects)'   ,
//                '''$subjects = concat-1($c = concat-2($c = true))''': 'selected(/s_f_v1/subjects, /s_f_v1/subjects)'   ,
        ].each {
            def h = '''$subjects = $subjects and $subject = concat( if($subjects = 'math' and $c = true,'true','false'))'''
            println('evaluating: ' + it.key)
            def path = Form.getAbsoluteBindingXPath(it.key, form.getQuestion('ps'))
            println path
            def compatibleXPath = ODKXpathUtil.makeODKCompatibleXPath(form, path, false)


            println compatibleXPath

            assertEquals it.value, compatibleXPath
        }
    }

    void testBooleanConversion() {
        assertEquals booleanConversion.xml,toODK(booleanConversion.form, true)
    }


    String toODK(String markup, boolean oxd = false) {
        toODK(toForm(markup), oxd)
    }

    static Form toForm(String markup) {
        Util.createParser(markup).study().forms[0]
    }

    String toODK(Form form, boolean oxd = false) {
        serializer.oxdConversion = oxd
        serializer.toXForm(form)
    }
}
