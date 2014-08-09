package org.openxdata.markup

import au.com.bytecode.opencsv.CSVWriter
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/3/13
 * Time: 4:17 AM
 * To change this template use File | Settings | File Templates.
 */
class DynamicBuilderTest extends GroovyTestCase {

    def csv = """Country,*District,School
Uganda,Kampala,Macos
Uganda,Kampala,Bugiroad
Kenya,Nairobi,Machaccos
Kenya,Nairobi,Lala
Kenya,Nairobi,Langley
Kenya,Nairobi,Kikuyu
"""
    //added space specifically on kampala to make sure it is trimmed
    def csvWithSpace = """Country,*District,School
Uganda,Kampala,Macos
,   Kampala   ,Bugiroad
Kenya,Nairobi,Machaccos
,,Lala
,,Langley
,,Kikuyu
"""

    def dynInstance = '''$root,district,school
Uganda,Kampala,Macos
Uganda,Kampala,Bugiroad
Kenya,Nairobi,Machaccos
Kenya,Nairobi,Lala
Kenya,Nairobi,Langley
Kenya,Nairobi,Kikuyu
'''
    def csvWithVariables = '''Country,*District,School
$ug Uganda,Kampala,Macos
$ug Uganda,Kampala,$maco2 Macos
$ug2 Uganda,$km Kampala,Bugiroad
$ky Kenya,Nairobi,Machaccos
Kenya,$nm2 Nairobi,Lala
$kn Kenya,Nairobi,Langley
$kn Kenya,Nairobi,Langley
Kenya,Nairobi,Kikuyu
'''
    def oxdIncompatibleCSV = '''Country,*District,School
Uganda,Kampala,Macos
Kenya,Kampala,Lala
Kenya,Nairobi,Langley
Kenya,Nairobi,Macos
'''


    DynamicBuilder builder = new DynamicBuilder();

    public void testParse() {
        builder.csvSrc = new StringBuilder(csv)
        builder.parse()

        checkQuestionContent()
    }

    private void checkQuestionContent() {
        assertEquals 'Expecting 3 questions', 3, builder.questions.size()

        SingleSelectQuestion qn = builder.questions.find { it instanceof SingleSelectQuestion }
        assertEquals 'Dingle select has 2 options', 2, qn.options.size()

        def districtDynInstance = builder.dynamicOptions[Util.getBindName('District')]
        assertEquals 'Expecting 2 options for dirstrict', 2, districtDynInstance.size()


        def districtQn = builder.questions.find { it.text.equals('District') }
        assertEquals Util.getBindName('District'), districtQn.binding
        assertTrue districtQn.required

        def dynQn3 = builder.dynamicOptions[Util.getBindName('School')]
        assertEquals 'Expecting 3 option for school', 6, dynQn3.size()
    }

    public void testQuickParse() {
        Study.quickParse.set(true)
        def lines = csv.split('\n')
        lines.each { builder.appendLine(it) }
        builder.parse()
        Study.quickParse.set(false)

        assertEquals 3, builder.csvSrc.toString().split('\n').length
    }


    public void testQuickParseWithFile() {

        Study.quickParse.set(true)
        def resourceFolder = Fixtures.setFormDirectory()

        DynamicBuilder builder = new DynamicBuilder();
        builder.csvFilePath = "$resourceFolder/quarters.csv"

        builder.parse()

        assertNotNull builder.parsedCsv

        assertEquals 3, builder.csvSrc.toString().split('\n').length
        Study.quickParse.set(false)
    }

    public void testOxdIncompatibleCSV() {
        builder.csvSrc = new StringBuilder(oxdIncompatibleCSV)

        builder.parse()

        assert builder.questions.size() == 3
        assert builder.dynamicOptions.size() == 2
        assert builder.dynamicOptions.district.size() == 3
        assert builder.dynamicOptions.school.size() == 4

    }

    public void testAppend() {
        def lines = csv.split('\n')
        lines.each { builder.appendLine(it) }
        builder.parse()
        checkQuestionContent()
    }

    public void testFillUpSpace() {

        def spaceCsv = DynamicBuilder.toStringArrayList(csvWithSpace)

        builder.fillUpSpace(spaceCsv)

        assertEquals DynamicBuilder.toStringArrayList(csv).toString(), spaceCsv.toString()


    }

    public void testDynamicInstanceBuilding() {

        DynamicBuilder builder = new DynamicBuilder();

        builder.csvSrc = new StringBuilder(dynInstance)

        builder.parse()

        assertEquals 2, builder.dynamicOptions.size()
        assertEquals 0, builder.questions.size()

        Form form = new Form("Form")
        form.addQuestion(new SingleSelectQuestion("Root"))
        builder.addQuestionsToForm(form)

        assertEquals 2, form.dynamicOptions.size()

        assertEquals 2, form.questions[0].options.size()

        try {
            form = new Form("Form")
            form.addQuestion(new SingleSelectQuestion("Blah"))
            builder.addQuestionsToForm(form)
            fail("Expection a not found validation exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("SingleSelect question with id [")
        }

        try {
            form = new Form("Form")
            form.addQuestion(new TextQuestion("Root"))
            builder.addQuestionsToForm(form)
            fail("Expection a not found validation exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("is not a SingleSelect Question")
        }


        try {
            form = new Form("Form")
            form.addQuestion(new SingleSelectQuestion("Root"))
            form.dynamicOptions.put('school', [])
            builder.addQuestionsToForm(form)
            fail("Expection a not found validation exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("have duplicate columns in your csv files")
        }

    }

    public void testCsvImport() {

        def resourceFolder = Fixtures.setFormDirectory()

        DynamicBuilder builder = new DynamicBuilder();
        builder.csvFilePath = "$resourceFolder/quarters.csv"

        builder.parse()

        assertNotNull builder.parsedCsv

        builder = new DynamicBuilder();
        builder.csvFilePath = /bad path/

        try {
            assertNull builder.parsedCsv
            builder.parse()
            fail('Expecting file not found')
        } catch (FileNotFoundException ex) {
            //do nothins
        }

        builder = new DynamicBuilder();
        builder.csvFilePath = 'quarters.csv'
        System.setProperty('form.dir', resourceFolder)

        builder.parse()

        assertNotNull builder.parsedCsv


    }

    public void testDynamicOptionWithVariables() {
        builder.csvSrc = new StringBuilder(csvWithVariables)
        builder.parse()

        assertEquals 'Expecting 3 questions', 3, builder.questions.size()
        SingleSelectQuestion qn = builder.questions.find { it instanceof SingleSelectQuestion }

        assertEquals 'Dingle select has 5 options', 5, qn.options.size()
        def districtDynInstance = builder.dynamicOptions[Util.getBindName('District')]

        assertEquals 'Expecting 6 options for district', 6, districtDynInstance.size()
        def districtQn = builder.questions.find { it.text.equals('District') }

        assertEquals Util.getBindName('District'), districtQn.binding
        assertTrue districtQn.required

        def dynQn3 = builder.dynamicOptions[Util.getBindName('School')]
        assertEquals 'Expecting 3 option for school', 7, dynQn3.size()

    }

    def csvWithSameChildrenDifferentParents = '''Region,Sub_Region,    City
                    Africa,    Kenya,    Eldoret
                    Europe,    Kenya,    Eldoret
                    America,    Kenya,    Eldoret
                    America,    Kenya,    Eldoret
                    America,    Kenya,    Eldoret
                    Africa,    Kenya,    Eldoret
                    Europe,    Kenya,    Eldoret'''

    public void testOxdInCompatibleCSV2() {
        builder.csvSrc = new StringBuilder(csvWithSameChildrenDifferentParents)

        builder.parse()

        assert builder.questions.size() == 3
        assert builder.dynamicOptions.size() == 2
        assert builder.dynamicOptions.sub_region.size() == 3
        assert builder.dynamicOptions.city.size() == 3

    }

    void testDynamicInstanceOnlyBuilding() {
        def src = '''### stdy
## form

Qn 1

dynamic_instance {
parent,cities
uganda,kampala
kenya,nairobi "M"
uganda,entebbe
}
'''

        def form = Util.createParser(src).study().forms[0]

        assert form.questions.size() == 1
        assert form.dynamicOptions.size() == 1
        assert form.dynamicOptions.get('cities').size() == 3
    }

    public void ignoreTestFillUpSpace2() {
        def file = new File(/C:\Users\kay\Dropbox\OMNI\snv database\community wash\community forms\lists.csv/)

        builder.csvFilePath = file

        def csv = DynamicBuilder.toStringArrayList(file.text)

        builder.fillUpSpace(csv)

        StringWriter stringWriter = new StringWriter()
        CSVWriter writer = new CSVWriter(stringWriter)

        writer.writeAll(csv)

        def file2 = new File(file.absolutePath + ".filled.csc")
        file2.text = stringWriter.toString()

    }

}
