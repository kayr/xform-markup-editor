package org.openxdata.markup.transformers.impl

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.TestUtils

/**
 * Created by user on 7/1/2017.
 */
class OdkMetaDataTransformerTest extends GroovyTestCase {


    void test_MetaDataTranformer() {

        def f = '''
        
        @odkmetadata
        ## F 
        
        Q1
'''

        def outMarkup = Converter.from(FORMAT.MARKUP, String).to(FORMAT.MARKUP).convert(f)


        def expected = '''## F


Q1


@absoluteid meta
group { 

    @absoluteid __start
    @invisible
    @bind:jr:preload timestamp
    @bind:jr:preloadParams start
    @dateTime
    Start Time


    @absoluteid __end
    @invisible
    @bind:jr:preload timestamp
    @bind:jr:preloadParams end
    @dateTime
    End Time


    @absoluteid __today
    @invisible
    @bind:jr:preload date
    @bind:jr:preloadParams today
    @date
    Today


    @absoluteid instanceID
    @invisible
    @calculate concat('uuid:',uuid())
    Instance ID


}'''

        assertEquals TestUtils.trimAllLines(expected), TestUtils.trimAllLines(outMarkup)
    }
}
