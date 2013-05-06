package org.openxdata.markup.serializer.layout

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/6/13
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
class LongTextLayout extends Layout {

    int getWidgetHeight(String widgetName) {
        return 25 * 4;
    }

    @Override
    int getTTHeight() {
        return 25 + 25 * 4
    }

    @Override
    String getInputType() {
        return 'TextArea'
    }

    int getWidth() {
        return 250
    }
}
