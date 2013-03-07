package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/3/13
 * Time: 4:17 AM
 * To change this template use File | Settings | File Templates.
 */
class DynamicBuilderTest extends GroovyTestCase {

    def csv = """Country,District,School
Uganda,Kampala,Macos
Uganda,Kampala,Bugiroad
Kenya,Nairobi,Machaccos
Kenya,Nairobi,Lala
Kenya,Nairobi,Langley
Kenya,Nairobi,Kikuyu
"""
     //added space specifically on kampala to make sure it is trimmed
    def csvWithSpace ="""Country,District,School
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

        DynamicQuestion districtQn = builder.questions.find {it.text.equals('District')}
        assertEquals 'Expecting 2 options for dirstrict', 2, districtQn.options.size()

        DynamicQuestion dynQn3 = builder.questions.find {it.text.equals('School')}
        assertEquals 'Expecting 3 option for school', 6, dynQn3.options.size()
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

}
