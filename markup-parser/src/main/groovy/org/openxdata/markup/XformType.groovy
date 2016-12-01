package org.openxdata.markup

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.ToString

/**
 * Created by kay on 11/30/2016.
 */
@ToString(includePackage = false)
@CompileStatic
enum XformType {
    TEXT('text'),
    BOOLEAN('boolean'),
    DECIMAL('decimal'),
    NUMBER('number'),
    SELECT('select'),
    SELECT1('select1'),
    SELECT1_DYNAMIC('select1_dynamic'),
    GPS('gps'),
    REPEAT('repeat'),
    GROUP('group'),
    BARCODE('barcode'),
    PICTURE('picture'),
    VIDEO('video'),
    AUDIO('audio'),
    DATE_TIME('datetime'),
    DATE('date'),
    TIME('time'),
    FORM('form')


    String value

    XformType(String value) {
        this.value = value
    }

    @PackageScope
    static XformType resolve(String markupType) {
        if(markupType == 'longtext')
            markupType = 'text'
        EnumSet.allOf(XformType).find { it.value == markupType } ?: TEXT
    }


}