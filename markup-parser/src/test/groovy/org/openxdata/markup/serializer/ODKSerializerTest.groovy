package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Form
import org.openxdata.markup.TestUtils
import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.exception.InvalidAttributeException

import static org.openxdata.markup.TestUtils.oxd2Odk
import static org.openxdata.markup.TestUtils.toForm
import static org.openxdata.markup.TestUtils.toODK
import static org.openxdata.markup.TestUtils.toOXD
import static org.openxdata.markup.deserializer.DeSerializerFixtures.getNestedGroups
import static org.openxdata.markup.deserializer.DeSerializerFixtures.getNestedGroups
import static org.openxdata.markup.deserializer.DeSerializerFixtures.nestedGroups
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
        assertEquals toODK(timeStamp.form, false, true), timeStamp.xml
    }

    void testFormWithRelativeValidation() {
        assertEquals formRelativeValidation.xml, toODK(formRelativeValidation.form)
    }

    void testOxdSampleForm() {
        assertEquals oxdSampleForm.xml, toODK(oxdSampleForm.form, false, true)
    }

    void testMultiSelectConversion() {
        assertEquals oxdSampleForm.xml, toODK(oxdSampleForm.form, false, true)
    }

    void testOxdExternalApp() {
        assertEquals formWithAppearanceComment.xml, toODK(formWithAppearanceComment.form, false, true)
    }

    void testSkipActionsAndLogic() {
        serializer.numberBindings = true
        serializer.numberQuestions = true
        assertEquals formSkipLogicAndActions.xml, toODK(formSkipLogicAndActions.form, true)
    }

    void testRegex() {
        //'length(.) = 4'                    : '4',// this is not supported in odk
        [
                'length(.) = 4'                    : null,
                'length(.) = "kdjjsd"'             : null,
                'length(.) = "kdjjsd" and 5 = 6'   : null,
                'length(.) = true() and 5 = 6'     : null,
                'length(.) = /form/field and 5 = 6': null,
                'length(.) = /ef/fr'               : '/ef/fr',
                'length(.) = ef/fr'                : '/ef/fr'
        ].each { reg ->
            def jrCount = ODKXpathUtil.getOXDJRCountOnRepeatValidation(reg.key)
            assertEquals reg.value, jrCount
        }
    }

    void testToODKMultiSelect() {
        Form form = toForm(multiSelectConversion.form)

        [
                '$s = \'calculus\' and ($ps != null or (3-4) = 9 or $s = \'grades\') and $s = \'biology\''                       :
                        "selected(/f/s, 'calculus') and (/f/ps != null or (3-4) = 9 or selected(/f/s, 'grades')) and selected(/f/s, 'biology')",
                '$s = \'calculus\' and $s != \'biology\''                                                                        :
                        "selected(/f/s, 'calculus') and not(selected(/f/s, 'biology'))",
                '\'calculus\' = $s and $s != \'biology\''                                                                        :
                        "selected(/f/s, 'calculus') and not(selected(/f/s, 'biology'))",
                '$s = \'calculus\' and $s != \'biology,calculus,math\''                                                          :
                        "selected(/f/s, 'calculus') and not(selected(/f/s, 'biology') or selected(/f/s, 'calculus') or selected(/f/s, 'math'))",
                '$ps = \'calculus\' and $s > \'biology\''                                                                        :
                        "/f/ps = 'calculus' and /f/s > 'biology'",
                '$s = calc() and $ps = true'                                                                                     :
                        'selected(/f/s, calc()) and /f/ps = true',
                '$c = true() and $c = $c2'                                                                                       :
                        '/f/c = \'true\' and /f/c = /f/c2',
                '$c = \'true\' and $ps = true'                                                                                   :
                        '/f/c = \'true\' and /f/ps = true',
                '$s = calc() and ($s = "calculus" or $s != "calculus" and ($s != "calculus"))and $ps = true and $s != "calculus"':
                        "selected(/f/s, calc()) and (selected(/f/s, 'calculus') or not(selected(/f/s, 'calculus')) and (not(selected(/f/s, 'calculus'))))and /f/ps = true and not(selected(/f/s, 'calculus'))",
                '$s = $s'                                                                                                        :
                        'selected(/f/s, /f/s)',
                '''$s = $s and $s = concat( if($s = 'math' and $c = true and $c = true ,'true','false'))'''                      :
                        "selected(/f/s, /f/s) and selected(/f/s, concat( if(selected(/f/s, 'math') and /f/c = 'true' and /f/c = 'true' ,'true','false')))",
                '''$s = f1($c = f1($s = f1($c = true)))'''                                                                       :
                        "selected(/f/s, f1(/f/c = string(f1(selected(/f/s, f1(/f/c = 'true'))))))",
                '''$s = concat-1($c = true)'''                                                                                   :
                        "selected(/f/s, concat-1(/f/c = 'true'))",
                '''$s'''                                                                                                         :
                        "/f/s"
        ].each {


            def path = Form.getAbsoluteBindingXPath(it.key, form.getElement('ps'))
            def compatibleXPath = ODKXpathUtil.makeODKCompatibleXPath(form, path, false)
            assertEquals it.value, compatibleXPath
        }
    }

    void testBooleanConversion() {
        assertEquals booleanConversion.xml, toODK(booleanConversion.form, false, true)
    }

    void testAppearanceAndLayoutAttributes() {
        assertEquals formWithLayoutAttributes.xml, toODK(Fixtures.formWithLayoutAndBindAttributes,)
    }

    void testAddingMetaInstanceId() {
        assertEquals oxdSampleForm.xmlWithMeta, toODK(oxdSampleForm.form, false, true, true)
    }

    void testNestedGroup() {
        def oxd = ODKSerializer.oxd2Odk(nestedGroups.xml, true)
        assertEquals nestedGroups.oxd2OdkXml, oxd
    }

    void testNestedGroupNumber() {
        def oxd = toODK(toForm(nestedGroups.markUp),true)
        assertEquals nestedGroups.odkNumberd, oxd
    }

}
