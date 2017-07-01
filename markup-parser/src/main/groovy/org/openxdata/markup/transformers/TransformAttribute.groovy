package org.openxdata.markup.transformers

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by user on 6/19/2017.
 */
@CompileStatic
@ToString(includePackage = false)
class TransformAttribute {
    int    line
    String annotation, param
}
