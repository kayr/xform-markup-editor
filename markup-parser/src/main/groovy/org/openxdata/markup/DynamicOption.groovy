package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
class DynamicOption extends Option implements IOption {

    String parent

    DynamicOption(String parent, String child) {
        super(child)
        this.parent = parent
    }

    String getChild(){
        return option
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof DynamicOption)) return false

        DynamicOption that = (DynamicOption) o

        if (child != that.child) return false
        if (parent != that.parent) return false

        return true
    }

    int hashCode() {
        int result
        result = (parent != null ? parent.hashCode() : 0)
        result = 31 * result + (child != null ? child.hashCode() : 0)
        return result
    }
}
