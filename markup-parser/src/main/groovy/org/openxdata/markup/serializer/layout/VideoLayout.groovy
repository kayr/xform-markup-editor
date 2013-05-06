package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder

/**
 <Item WidgetType="GroupBox" HelpText="Video" Binding="LEFT20pxTOP560px" Left="20px" Top="560px" Width="200px" Height="125px" TabIndex="11" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" borderStyle="dashed">
 <Item WidgetType="VideoAudio" Text="Click to play" Binding="video" Left="45px" Top="45px" TabIndex="0" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
 <Item WidgetType="Button" Text="Browse" HelpText="browse" Binding="browse" ParentBinding="video" Left="10px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
 <Item WidgetType="Button" Text="Clear" HelpText="clear" Binding="clear" ParentBinding="video" Left="120px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif"/>
 <Item WidgetType="Label" Text="Video" Binding="LEFT20pxTOP560px" Left="0px" Top="0px" Width="100%" Height="20px" TabIndex="0" color="white" fontWeight="bold" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif" backgroundColor="rgb(143, 171, 199)" textAlign="center" HeaderLabel="true"/>
 </Item>
 */

class VideoLayout extends Layout {


    void add(MarkupBuilder xml) {
        def binding = "LEFT20pxTOP${top}px"
        def height = addGroupBox(binding, xml) {
            xml.Item(WidgetType: "VideoAudio", Text: "Click to play", Binding: qn.binding, Left: "45px", Top: "45px",
                    TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Button", Text: "Browse", HelpText: "browse", Binding: "browse", ParentBinding: qn.binding,
                    Left: "10px", Top: "85px", Width: "90px", Height: "30px", TabIndex: "0", fontSize: "16px",
                    fontFamily: FONT)
            xml.Item(WidgetType: "Button", Text: "Clear", HelpText: "clear", Binding: "clear",
                    ParentBinding: qn.binding, Left: "120px", Top: "85px", Width: "90px", Height: "30px",
                    TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            xml.Item(WidgetType: "Label", Text: qn.text, Binding: binding, Left: "0px", Top: "0px", Width: "100%",
                    Height: "20px", TabIndex: "0", color: "white", fontWeight: "bold", fontSize: "16px",
                    fontFamily: "Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif",
                    backgroundColor: "rgb(143, 171, 199)", textAlign: "center", HeaderLabel: "true")
        }

    }


    void setBeginIdx(int idx) {
        top = idx
      //  return idx + 125 + 10
    }

    int getWidgetHeight(String widgetName) {
        return 125
    }

    int getTTHeight() {
        return 125
    }

}

