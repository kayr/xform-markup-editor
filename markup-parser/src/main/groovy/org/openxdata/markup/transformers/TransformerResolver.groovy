package org.openxdata.markup.transformers

import groovy.transform.CompileStatic
import org.openxdata.markup.Attrib
import org.openxdata.markup.FLAGS
import org.openxdata.markup.Form
import org.openxdata.markup.IFormElement
import org.openxdata.markup.transformers.impl.JRCountTransformer
import org.openxdata.markup.transformers.impl.SelectOtherTransformer
import org.openxdata.markup.util.Assert

/**
 * Created by user on 6/17/2017.
 */
@CompileStatic
class TransformerResolver {

    static TransformerResolver instance = new TransformerResolver()

    private Map<String, Transformer> transformers = [:]

    private TransformerResolver() {
        addTransformer('jrcount', new JRCountTransformer())
        addTransformer('selectother', new SelectOtherTransformer())
    }


    boolean canHandle(String transformAnnotation) {
        return transformers.containsKey(transformAnnotation)
    }


    void transform(EnumSet<FLAGS> flagsEnumSet, IFormElement element, TransformAttribute transformAttribute) {

        def annotation = transformAttribute.annotation
        if (!canHandle(annotation))
            throw new RuntimeException("No Transformer found for annotation [${annotation}]")

        def transformer = transformers.get(annotation)

        transformer.transform(flagsEnumSet, element, transformAttribute)
    }

    void doTransformations(EnumSet<FLAGS> flags, Form form) {

        form.allElements.each { e ->
            e.transformAttributes.each { k, v -> transform(flags, e, v) }
        }

    }


    void addTransformer(String annotation, Transformer transformer) {

        Assert.isFalse(
                Attrib.allowedAttributes.contains(annotation) || Attrib.types.contains(annotation),
                "You cannot have transformer for annotation($annotation) because it natively exists")

        Assert.isFalse(transformers.containsKey(annotation), "Annotation processor for [$annotation] already exists")


        transformers[annotation] = transformer
    }


}
