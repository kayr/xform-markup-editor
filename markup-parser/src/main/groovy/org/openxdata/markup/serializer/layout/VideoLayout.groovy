package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder

class VideoLayout extends Layout {


    void add(MarkupBuilder xml) {
        def binding = "LEFT20pxTOP${top}px"
        addGroupBox(binding, xml) {
            xml.Item(WidgetType: "VideoAudio", Text: "Click to play", Binding: qn.getBinding(numberBindings), Left: "45px", Top: "45px",
                    TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Button", Text: "Browse", HelpText: "browse", Binding: "browse", ParentBinding: qn.getBinding(numberBindings),
                    Left: "10px", Top: "85px", Width: "90px", Height: "30px", TabIndex: "0", fontSize: "16px",
                    fontFamily: FONT)
            xml.Item(WidgetType: "Button", Text: "Clear", HelpText: "clear", Binding: "clear",
                    ParentBinding: qn.getBinding(numberBindings), Left: "120px", Top: "85px", Width: "90px", Height: "30px",
                    TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Label", Text: qn.getText(numberText), Binding: binding, Left: "0px", Top: "0px", Width: "100%",
                    Height: "20px", TabIndex: "0", color: "white", fontWeight: "bold", fontSize: "16px",
                    fontFamily: "Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif",
                    backgroundColor: "rgb(143, 171, 199)", textAlign: "center", HeaderLabel: "true")
        }

    }


    void setTopPosition(int idx) {
        top = idx
    }

    int getWidgetHeight(String widgetName) {
        return 125
    }

    int getTTHeight() {
        return 125
    }

}

