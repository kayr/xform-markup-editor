package org.openxdata.markup.transformers.impl

import org.openxdata.markup.Converter
import org.openxdata.markup.FORMAT
import org.openxdata.markup.TestUtils

/**
 * Created by user on 7/1/2017.
 */
class ODKMetaDataTranformaerTest extends GroovyTestCase {

    void test_MetaDataTranformer() {

        def f = '''
        
        @odkmetadata
        ## F 
        
        Q1
'''

        def outMarkup = Converter.from(FORMAT.MARKUP, String).to(FORMAT.MARKUP).convert(f)


        def expected = '''## F


Q1


@absoluteid __meta
group { 

    @absoluteid __start
    @invisible
    @bind:jr:preload timestamp
    @bind:jr:preloadParams start
    @datetime
    Start Time


    @absoluteid __end
    @invisible
    @bind:jr:preload timestamp
    @bind:jr:preloadParams end
    @datetime
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


    @invisible
    @calculate once(concat('uuid:',uuid()))
    Unique ID


}'''

        assertEquals TestUtils.trimAllLines(expected), TestUtils.trimAllLines(outMarkup)
    }
}
