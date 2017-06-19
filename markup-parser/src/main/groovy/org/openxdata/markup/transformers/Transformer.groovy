package org.openxdata.markup.transformers

import org.openxdata.markup.FLAGS
import org.openxdata.markup.IFormElement

/**
 * Created by user on 6/17/2017.
 */
interface Transformer {
    void transform(EnumSet<FLAGS> flags, IFormElement element,  TransformAttribute transformAttribute)
}
