package org.openxdata.markup.serializer.layout

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/5/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
class TextLayout extends Layout {
    @Override
    int getTTHeight() {
        return 25 - 8 + 25
    }

    @Override
    String getInputType() {
        switch (qn.type) {
            case 'date':
                return 'DatePicker'
            case 'dateTime':
                return 'DateTimeWidget'
            case 'time':
                return 'TimeWidget'
            default:
                return 'TextBox'
        }
    }
}

