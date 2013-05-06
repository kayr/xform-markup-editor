package org.openxdata.markup.serializer.layout

import org.openxdata.markup.IQuestion
import groovy.xml.MarkupBuilder

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/5/13
 * Time: 9:26 PM
 * To change this template use File | Settings | File Templates.
 */
class RepeatLayout extends Layout {

    List<Layout> questions = []
    int left = 0

    void setQn(IQuestion qn) {
        for (it in qn.questions) {
            Layout layout = serializer.getLayout(it)
            layout.left = left
            left = layout.width + left
            questions << layout
        }
        super.setQn(qn)
    }

    void add(MarkupBuilder xml) {

        int height = addLabel(xml, [fontWeight:"bold" ,fontStyle:"italic"])

        top += height


        questions.each {
            it.addRptLabel(xml)
        }

        top += 25

//            <Item WidgetType="GroupBox" HelpText="Repeat Question header" Binding="repeat_question_header" Left="20px" Top="430px" Width="425px" Height="100px" TabIndex="10" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Repeated="1">
//            <Item WidgetType="Button" Text="Add New" HelpText="addnew" Binding="addnew" Left="10px" Top="55px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
//            <Item WidgetType="ListBox" HelpText="rpt question 1" Binding="rpt_question_1" Left="10px" Top="10px" Width="200px" Height="25px" TabIndex="1" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
//            <Item WidgetType="CheckBox" Text="dsksd" HelpText="dsksd" Binding="dsksd" ParentBinding="rpt_question_2" Left="215px" Top="10px" TabIndex="2" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
//            </Item>


        xml.Item(WidgetType: "GroupBox", HelpText: qn.comment != null ? qn.comment : qn.text, Binding: qn.binding,
                Left: "0px", Top: "${top}px", Width: "${width}px", Height: "100px", TabIndex: getNextId(), fontSize: "16px",
                fontFamily: FONT, Repeated: "1") {
            xml.Item(WidgetType: "Button", Text: "Add New", HelpText: "addnew", Binding: "addnew", Left: "10px",
                    Top: "55px", Width: "90px", Height: "30px", TabIndex: "0", fontSize: "12px", fontFamily: FONT)

            questions.each {
                it.addRepeat(xml)
            }
        }
    }

    void setBeginIdx(int idx) {
        top = idx
        questions.each {it.setBeginIdx(top + 25)}

    }

    int getWidth() {
        return qn.questions.size() * 200
    }

    int getTTHeight() {
        //return idx + 25 + 25 + 100 + 10
        return  25 + 25 + 100 + 10
    }
}
