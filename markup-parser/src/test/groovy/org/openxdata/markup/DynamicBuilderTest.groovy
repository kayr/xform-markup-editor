package org.openxdata.markup

import au.com.bytecode.opencsv.CSVWriter

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
    def csvWithSpace ="""Country,*District,School
Uganda,Kampala,Macos
,   Kampala   ,Bugiroad
Kenya,Nairobi,Machaccos
,,Lala
,,Langley
,,Kikuyu
"""



    DynamicBuilder builder = new DynamicBuilder();

    public void testParse(){
        builder.csvSrc = csv
        builder.parse()

        checkQuestionContent()
    }

    private void checkQuestionContent() {
        assertEquals 'Expecting 3 questions', 3, builder.questions.size()

        SingleSelectQuestion qn = builder.questions.find {it instanceof SingleSelectQuestion}
        assertEquals 'Dingle select has 2 options', 2, qn.options.size()

        def districtDynInstance = builder.dynamicOptions[ Util.getBindName('District')]
        assertEquals 'Expecting 2 options for dirstrict', 2, districtDynInstance.size()


        def districtQn = builder.questions.find {it.text.equals('District')}
        assertEquals Util.getBindName('District'),districtQn.binding
        assertTrue districtQn.required

        def dynQn3 =builder.dynamicOptions[ Util.getBindName('School')]
        assertEquals 'Expecting 3 option for school', 6, dynQn3.size()
    }



    public void testAppend(){
        def lines = csv.split('\n')
        lines.each {builder.appendLine(it)}
        builder.parse()
        checkQuestionContent()
    }

    public void testFillUpSpace(){

        def spaceCsv = DynamicBuilder.toStringArrayList(csvWithSpace)

        builder.fillUpSpace(spaceCsv)

        assertEquals DynamicBuilder.toStringArrayList(csv).toString(),spaceCsv.toString()



    }

    public void rtestLol(){

        def text = new File('i:/fac.csv').text

        def csv = DynamicBuilder.toStringArrayList(text)

        builder.fillUpSpace(csv)

        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter)

        writer.writeAll(csv)
        def file2 = new File('i:/fac2.csv')
       if(!file2.exists()) file2.createNewFile()

        file2.text = stringWriter.toString()

    }

}
