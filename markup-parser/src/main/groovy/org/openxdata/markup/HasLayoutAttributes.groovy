package org.openxdata.markup

/**
 * Created by kay on 6/24/2016.
 */
trait HasLayoutAttributes {

    Map<String, String> layoutAttributes = [:]
}

trait HasBindAttributes extends HasIdentifier{
    Map<String, String> bindAttributes = [:]

}
