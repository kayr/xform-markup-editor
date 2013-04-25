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



    DynamicBuilder builder = new DynamicBuilder();

    public void testParse() {
        builder.csvSrc = csv
        builder.parse()

        checkQuestionContent()
    }

    private void checkQuestionContent() {
        assertEquals 'Expecting 3 questions', 3, builder.questions.size()

        SingleSelectQuestion qn = builder.questions.find {it instanceof SingleSelectQuestion}
        assertEquals 'Dingle select has 2 options', 2, qn.options.size()

        def districtDynInstance = builder.dynamicOptions[Util.getBindName('District')]
        assertEquals 'Expecting 2 options for dirstrict', 2, districtDynInstance.size()


        def districtQn = builder.questions.find {it.text.equals('District')}
        assertEquals Util.getBindName('District'), districtQn.binding
        assertTrue districtQn.required

        def dynQn3 = builder.dynamicOptions[Util.getBindName('School')]
        assertEquals 'Expecting 3 option for school', 6, dynQn3.size()
    }



    public void testAppend() {
        def lines = csv.split('\n')
        lines.each {builder.appendLine(it)}
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

        builder.csvSrc = dynInstance

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
            form.dynamicOptions.put('school',[])
            builder.addQuestionsToForm(form)
            fail("Expection a not found validation exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("have duplicate columns in your csv files")
        }

    }

    public void testCsvImport (){

       def resourceFolder = Fixtures.setFormDirectory()

        DynamicBuilder builder = new DynamicBuilder();
        builder.csvFile = "$resourceFolder/quarters.csv"

        builder.parse()

        assertNotNull builder.parsedCsv

        builder = new DynamicBuilder();
        builder.csvFile = /bad path/

        try{
            assertNull builder.parsedCsv
            builder.parse()
            fail('Expecting file not found')
        }   catch (FileNotFoundException ex){
              //do nothins
        }

        builder = new DynamicBuilder();
        builder.csvFile = 'quarters.csv'
        System.setProperty('form.dir',resourceFolder)

       builder.parse()

        assertNotNull builder.parsedCsv



    }

    public void rtestLol() {

        def sourceFile = new File('C:\\Users\\kay\\Documents\\My Dropbox\\OMNI\\snv database\\markup-xforms\\schools.csv')

        def csv = DynamicBuilder.toStringArrayList(sourceFile.text)

        builder.fillUpSpace(csv)

        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter)

        writer.writeAll(csv)
        def file2 = new File("i:/${sourceFile.name-'.csv'}-clean.csv")
        if (!file2.exists()) file2.createNewFile()

        file2.text = stringWriter.toString()

    }

}
