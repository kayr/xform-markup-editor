package org.openxdata.markup

import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/30/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
class Option implements IOption {

    String option
    String bind

    Option(String option) {
        setOption(option)
    }

    void parseBind() {
        if (option[0] == '$') {
            def tmpBind = option.find(/[$][a-z][a-z0-9_]*/)
            //make sure bind is at the beginning
            if (option == null || option.indexOf(tmpBind) > 0)
                throw new ValidationException("""Option [$option] has an invalid id.
 An Id should start with lower case characters follow by low case characters, numbers or underscores""")
            option = option.replaceFirst(/[$][a-z][a-z0-9_]*/, '').trim()
            bind = tmpBind - '$'
        }
        else {
            bind = Util.getBindName(option)
        }
    }

    void setOption(String option) {
        this.option = option
        parseBind()
    }

    @Override
    String getText() {
        return option
    }

    @Override
    String getBind() {
        return bind
    }
}
