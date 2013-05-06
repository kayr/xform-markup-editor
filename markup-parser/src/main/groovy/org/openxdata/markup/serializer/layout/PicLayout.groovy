package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/5/13
 * Time: 9:24 PM
 * To change this template use File | Settings | File Templates.
 */
class PicLayout extends Layout {

    void add(MarkupBuilder xml) {
        String binding = "LEFT20pxTOP${top}px"
        def height = addGroupBox(binding, xml) {
            //<Item WidgetType="Picture" Binding="picture" Left="10px" Top="35px" Width="185px"
            // Height="155px" TabIndex="0" fontSize="16px" backgroundColor="transparent"/>
            xml.Item(WidgetType: "Picture", Text: "Click to play", Binding: qn.getBinding(numberBindings), Left: "10px", Top: "35px",
                    Width: "185px",Height:"155px", TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Button", Text: "Browse", HelpText: "browse", Binding: "browse", ParentBinding: qn.getBinding(numberBindings),
                    Left: "10px", Top: "200px", Width: "90px", Height: "30px", TabIndex: "0", fontSize: "16px",
                    fontFamily: FONT)

            //Left="10px" Top="200px" Width="90px" Height="30px"
            xml.Item(WidgetType: "Button", Text: "Clear", HelpText: "clear", Binding: "clear",
                    ParentBinding: qn.getBinding(numberBindings), Left: "120px", Top: "200px", Width: "90px", Height: "30px",
                    TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Label", Text: qn.getText(numberText), Binding: binding, Left: "0px", Top: "0px", Width: "100%",
                    Height: "20px", TabIndex: "0", color: "white", fontWeight: "bold", fontSize: "16px",
                    fontFamily: "Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif",
                    backgroundColor: "rgb(143, 171, 199)", textAlign: "center", HeaderLabel: "true")
        }

    }

    void setBeginIdx(int idx) {
        top = idx
    }

    int getWidgetHeight(String widgetName) {
        return 245
    }

    int getTTHeight() {
        return 245
    }
}
