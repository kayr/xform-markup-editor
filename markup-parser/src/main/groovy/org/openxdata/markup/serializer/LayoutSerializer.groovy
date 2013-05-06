package org.openxdata.markup.serializer

import org.openxdata.markup.Form
import org.openxdata.markup.Page
import org.openxdata.markup.IQuestion
import org.openxdata.markup.RepeatQuestion
import groovy.xml.MarkupBuilder
import org.openxdata.markup.MultiSelectQuestion
import org.openxdata.markup.SingleSelectQuestion
import org.openxdata.markup.TextQuestion
import org.openxdata.markup.DynamicQuestion
import org.openxdata.markup.IOption

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/4/13
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
class LayoutSerializer {

    private static final String FONT = "Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, sans-serif"


    def wigdets = [gps: 'TextBox', string: 'TextBox']

    String generateLayout(Form form) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer);

        List<PageLayOut> pageLayouts = []

        form.pages.each {
            PageLayOut page = new PageLayOut(page: it)
            pageLayouts << page
            it.questions.each {
                page.addComponent(getLayout(it))
            }
        }

        xml.doubleQuotes = true
        xml.Form() {
            pageLayouts.each {
                it.add(xml)
            }
        }

        return writer.toString()

    }
//    static def types = ['number', 'decimal', 'date', 'boolean', 'time', 'datetime', 'picture', 'video', 'audio',
//            'picture', 'gps', 'barcode']
    Layout getLayout(IQuestion qn) {
        if (qn instanceof RepeatQuestion) {
            return new RepeatLayout(qn: qn)
        }

        if (qn instanceof SingleSelectQuestion ||
                qn instanceof DynamicQuestion) {
            return new SingleSelectLayout(qn: qn)
        }

        if (qn instanceof MultiSelectQuestion) {
            return new MultiLayout(qn: qn)
        }

        switch (qn.type) {
            case 'string':
            case 'number':
            case 'gps':
            case 'barcode':
            case 'decimal':
                return new TextLayout(qn: qn)
            case 'boolean':
                return new SingleSelectLayout(qn: qn)
            case 'video':
            case 'audio':
                return new VideoLayout(qn: qn)
            case 'picture':
                return new PicLayout(qn: qn)

        }

    }




    static def types = ['number', 'decimal', 'date', 'boolean', 'time', 'datetime', 'picture', 'video', 'audio',
            'picture', 'gps', 'barcode']


    int nextId

    def getNextId() {
        return nextId++
    }

    abstract class Layout {

        int top
        IQuestion qn
        def parentLayout

        int addLabel(MarkupBuilder xml) {
            def height = 25
            xml.Item(WidgetType: "Label", Text: qn.text, HelpText: qn.comment,
                    Binding: qn.binding, Left: "20px", Top: top + 'px',
                    Width: "200px", Height: "${height}px", TabIndex: "0", fontSize: "12px", fontFamily: FONT)
            return height
        }

        private int addInput(String widgetType, MarkupBuilder xml) {
            def height = getWidgetHeight(widgetType)
            xml.Item(WidgetType: widgetType, Text: (qn instanceof Map) ? qn.text : '',
                    HelpText: qn.comment == null ? qn.text : qn.comment, Binding: qn.binding,
                    Left: "40px", Top: top + 'px', Width: "200px", Height: "${height}px",
                    TabIndex: getNextId(), fontSize: "12px", fontFamily: FONT)
            return height
        }

        int addCheckBox(IOption option, MarkupBuilder xml) {
            def height = getWidgetHeight('CheckBox')
            xml.Item(WidgetType: 'CheckBox', Text: option.text, HelpText: option.text, Binding: option.bind,
                    ParentBinding: option.parent.binding, Left: "40px", Top: "${top}px", Width: "200px",
                    Height: "${height}px", TabIndex: getNextId(), fontSize: "12px", fontFamily: FONT)
            return height

        }

        int addGroupBox(String binding, MarkupBuilder xml, Closure closure) {
            def height = getWidgetHeight('GroupBox')
            def grpBinding = binding != null ? binding : "LEFT20pxTOP${top}px"
            xml.Item(WidgetType: "GroupBox", HelpText: "Video", Binding: grpBinding, Left: "40px", Top: top + "px",
                    Width: "200px", Height: height + "px", TabIndex: getNextId(), fontSize: "12px",
                    fontFamily: "Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif", borderStyle: "dashed") {
                closure.call()

            }
            return height
        }

        int getWidgetHeight(String widgetName) {
            switch (widgetName) {
                default:
                    return 25;
            }
        }

        void add(MarkupBuilder xml) {
            def height = addLabel(xml)
            top = top + height - 5//add space
            addInput(getInputType(), xml)
        }

        String getInputType() {
            'TextBox'
        }

        int setBeginIdx(int idx) {
            top = idx
            return idx + 25 + 25
        }

        int getWidth() {
            return 200
        }
    }



    class PageLayOut extends Layout {
        Page page
        int top = 20

        private List<Layout> components = []

        void add(MarkupBuilder xml) {
            xml.Page(Text: page.name, fontWeight: 'normal', fontSize: '16px', fontFamily: FONT, Binding: page.binding, Width: '900px', Height: '900px', backgroundColor: '') {
                components.each {
                    it.add(xml)
                }
            }
        }

        void addComponent(Layout layout) {
            top = layout.setBeginIdx(top)
            layout.parentLayout = this
            components << layout
        }

        int getIndex(Layout layout) {
            return components.indexOf(layout) + 1
        }
    }

    class RepeatLayout extends Layout {

        List<Layout> questions = []

        void setQn(RepeatQuestion qn) {
            qn.questions.each {
                Layout layout = getLayout(it)
                questions << layout
            }
        }

        void add(MarkupBuilder xml) {

        }
    }

    class TextLayout extends Layout {
    }

    class SingleSelectLayout extends Layout {

        String getInputType() {
            return 'ListBox'
        }
    }

    class MultiLayout extends Layout {

        void add(MarkupBuilder xml) {
            def height = addLabel(xml)
            top = top + height - 5
            MultiSelectQuestion multiQn = qn
            multiQn.options.each {
                height = addCheckBox(it, xml)
                top = top + height - 5
            }
        }

        int setBeginIdx(int idx) {
            top = idx
            return idx + (qn.options.size() * 25) + 20
        }
    }

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
                xml.Item(WidgetType: "Label", Text: "Video", Binding: binding, Left: "0px", Top: "0px", Width: "100%",
                        Height: "20px", TabIndex: "0", color: "white", fontWeight: "bold", fontSize: "16px",
                        fontFamily: "Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif",
                        backgroundColor: "rgb(143, 171, 199)", textAlign: "center", HeaderLabel: "true")
            }

        }


        int setBeginIdx(int idx) {
            top = idx
            return idx + 125 + 10
        }

        int getWidgetHeight(String type) {
            return 125
        }

    }

    class PicLayout extends Layout {
    }

}
