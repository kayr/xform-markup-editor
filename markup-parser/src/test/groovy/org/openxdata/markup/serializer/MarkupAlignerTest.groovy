package org.openxdata.markup.serializer
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 11/15/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
class MarkupAlignerTest extends GroovyTestCase {

    def inpurt = '''
//comment
//comment
### Sample Markup Study
@id simple_form_v1
## Simple Form
q1
//comment
q2
Sex
>male
>female
@boolean
Is pregnant
@number
What is your Age
Course units
>>Computer Science
>>Computer Science
//comment
@number
Score math
@validif . = 3
repeat{ Child details
    Child Name
    Child Sex
   >male
   //e434
    >female
   @number
    Child Age
}
d4
$>dynamic option
dynamic{
Region,Sub-Region,City
Washington,King,Seattle
}
//comment
'''

    def expected = '''//comment
//comment


### Sample Markup Study


@id simple_form_v1
## Simple Form


q1


//comment
q2


Sex
  >male
  >female


@boolean
Is pregnant


@number
What is your Age


Course units
  >>Computer Science
  >>Computer Science


//comment
@number
Score math


@validif . = 3
repeat{ Child details

        Child Name


        Child Sex
          >male
        //e434
          >female


        @number
        Child Age
}


d4
$>dynamic option


dynamic{
        Region,Sub-Region,City
        Washington,King,Seattle
}


//comment
'''

    void testAlign() {

        MarkupAligner formatter = new MarkupAligner(inpurt)

//        println formatter.align()
        assertEquals expected.trim(), formatter.align().trim()

    }
}
