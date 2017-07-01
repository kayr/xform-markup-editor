package org.openxdata.markup.transformers.impl

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.TestUtils
import org.openxdata.markup.exception.InvalidAttributeException

/**
 * Created by user on 7/1/2017.
 */
class SelectOtherTransformerTest extends GroovyTestCase {

    void test_thatOtherIsGeneratedForMultiSelect() {

        def f = '''## F
        
                Q1
                
                @selectother
                Sex
                >m
                >f
                
                Q2
                
                @selectother
                Classes
                >>c1
                >>c2
                '''

        def generated = Converter.from(FORMAT.MARKUP, String).to(FORMAT.MARKUP).convert(f)

        def expected = '''@id f_v1
                          ## F
                            
                          Q1
                            
                          Sex
                                >m
                                >f
                                >Other
                            
                          @id __sex_other
                          @showif $sex = 'other'
                          Sex (Other)
                            
                          Q2
                          
                          Classes
                            >>c1
                            >>c2
                            >>Other
                        
                        
                        @id __classes_other
                        @showif $classes = 'other'
                        Classes (Other)'''

        assertEquals TestUtils.deleteAllEmpty(expected), TestUtils.deleteAllEmpty(generated)
    }

    void test_ThatOtherIsNotGeneratedForOtherQuestionTypes() {

        def f = '''
                    ## F
                    
                    @selectother
                    Q1
                    '''

        shouldFail(InvalidAttributeException) {
            Converter.from(FORMAT.MARKUP, String).to(FORMAT.MARKUP).convert(f)
        }


    }
}
