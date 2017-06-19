package org.openxdata.markup.transformers

import org.openxdata.markup.Attrib
import org.openxdata.markup.FLAGS
import org.openxdata.markup.Form
import org.openxdata.markup.IFormElement

/**
 * Created by user on 6/17/2017.
 */
@Singleton
class TransformerResolver {

    private Map<String, Transformer> transformers = [:]


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

        if (Attrib.allowedAttributes.contains(annotation) || Attrib.types.contains(annotation))
            throw new RuntimeException("You cannot have transformer for annotation($annotation) because it natively exists")


        if (transformers.containsKey(annotation))
            throw new RuntimeException("Annotation processor for [$annotation] already exists")


        transformer[annotation] = transformer
    }


    def installDefaultTransformers() {
        transformers.put('jrcount', new JRCountTransformer())
    }

    {
        installDefaultTransformers()

    }


}
