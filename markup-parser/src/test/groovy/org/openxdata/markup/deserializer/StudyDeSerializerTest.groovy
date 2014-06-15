package org.openxdata.markup.deserializer;

import junit.framework.TestCase
import org.openxdata.markup.Fixtures

public class StudyDeSerializerTest extends TestCase {

    def deSerializer = new StudyDeSerializer()

    public void testToStudy() throws Exception {

        def study = deSerializer.toStudy(Fixtures.snvStudyXML)


        assertEquals "Snv Study",study.name

        assertEquals 2, study.forms.size()

        assertEquals 'Snv Form',study.forms[0].name

        assertEquals 'form2',study.forms[1].name
    }
}
