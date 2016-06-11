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

    def markUp2 = [raw: '''

### Simple Form


@id sample_markup_study_simple_form_v1
## Simple Form


#> Page1


Name


Sex
  >male
  >female


@boolean
Is pregnant


@number
What is your Age


Course units
  >>Computer Science
  >>            Math
  >>History


@number
Score math


@number
Score Computer Science


@number
Score History


Total score


@number
Number of children


repeat{ Child details

 Child Name


        Child Sex
          >male
          >female


        @number
        Child Age
}


Region
  >Washington
  >Texas
  >Africa



>Europe


@parent region
Sub-Region




$> sub_hyphen_region


@parent sub_hyphen_region
City
$> city


dynamic_instance{
        root,sub_hyphen_region
        washington, King
        washington, Pierce
        texas, King-Texas
        texas, Cameron
        africa, Uganda
        africa, Kenya
        europe, Netherlands
}


dynamic_instance{
        root,city
        king, Seattle
        king,               Redmond
        pierce, Tacoma

             pierce, Puyallup
        king_hyphen_texas, Dumont
        king_hyphen_texas, Finney
        cameron,             brownsville
        cameron, harlingen

            uganda, Kampala
        uganda, Masaka

             uganda,                        Mbale
        uganda, Mbarara

             kenya, Nairobi
        kenya, Kisumu
        kenya, Eldoret
        netherlands, Netherlandis
        netherlands, Another Netherlands
}
''',
            formatted: '''

### Simple Form


@id sample_markup_study_simple_form_v1
## Simple Form


#> Page1


Name


Sex
  >male
  >female


@boolean
Is pregnant


@number
What is your Age


Course units
  >>Computer Science
  >>Math
  >>History


@number
Score math


@number
Score Computer Science


@number
Score History


Total score


@number
Number of children


repeat{ Child details

        Child Name


        Child Sex
          >male
          >female


        @number
        Child Age
}


Region
  >Washington
  >Texas
  >Africa
  >Europe


@parent region
Sub-Region
$> sub_hyphen_region


@parent sub_hyphen_region
City
$> city


dynamic_instance{
        root,sub_hyphen_region
        washington, King
        washington, Pierce
        texas, King-Texas
        texas, Cameron
        africa, Uganda
        africa, Kenya
        europe, Netherlands
}


dynamic_instance{
        root,city
        king, Seattle
        king, Redmond
        pierce, Tacoma
        pierce, Puyallup
        king_hyphen_texas, Dumont
        king_hyphen_texas, Finney
        cameron, brownsville
        cameron, harlingen
        uganda, Kampala
        uganda, Masaka
        uganda, Mbale
        uganda, Mbarara
        kenya, Nairobi
        kenya, Kisumu
        kenya, Eldoret
        netherlands, Netherlandis
        netherlands, Another Netherlands
}
''']

    def mulitineMarkup2 = [
            raw: """

### Simple Form


@id sample_markup_study_simple_form_v1
## Simple Form


#> Page1


Name


'''
Sex
''''
  > ''' male
  this is a male teacher

  ''''
  >''''
  female
  ''''

  sinqle question


@boolean
'''this is a multiine
question
check it out

'''''


@number
What is your Age


Course units
  >>Computer Science
  >>            Math
  >>History


@number
Score math


@number
Score Computer Science


@number
Score History


Total score


@number
Number of children


repeat{ Child details

 Child Name


        '''
        Child Sex
        Child Sex
        Child Sex
        '''
          >'''
          male
          male
          male'''
          >female


        @number
        Child Age
}


Region
  >Washington
  >Texas
  >Africa



>Europe


@parent region
Sub-Region




\$> sub_hyphen_region


@parent sub_hyphen_region
City
\$> city


dynamic_instance{
        root,sub_hyphen_region
        washington, King
        washington, Pierce
        texas, King-Texas
        texas, Cameron
        africa, Uganda
        africa, Kenya
        europe, Netherlands
}


dynamic_instance{
        root,city
        king, Seattle
        king,               Redmond
        pierce, Tacoma

             pierce, Puyallup
        king_hyphen_texas, Dumont
        king_hyphen_texas, Finney
        cameron,             brownsville
        cameron, harlingen

            uganda, Kampala
        uganda, Masaka

             uganda,                        Mbale
        uganda, Mbarara

             kenya, Nairobi
        kenya, Kisumu
        kenya, Eldoret
        netherlands, Netherlandis
        netherlands, Another Netherlands
}
""",
            formatted: """

### Simple Form


@id sample_markup_study_simple_form_v1
## Simple Form


#> Page1


Name


'''
Sex
''''
  >''' male
  this is a male teacher

''''
  >''''
  female
''''


sinqle question


@boolean


'''this is a multiine
question
check it out

'''''


@number
What is your Age


Course units
  >>Computer Science
  >>Math
  >>History


@number
Score math


@number
Score Computer Science


@number
Score History


Total score


@number
Number of children


repeat{ Child details

        Child Name


        '''
        Child Sex
        Child Sex
        Child Sex
        '''
          >'''
          male
          male
        male'''
          >female


        @number
        Child Age
}


Region
  >Washington
  >Texas
  >Africa
  >Europe


@parent region
Sub-Region
\$> sub_hyphen_region


@parent sub_hyphen_region
City
\$> city


dynamic_instance{
        root,sub_hyphen_region
        washington, King
        washington, Pierce
        texas, King-Texas
        texas, Cameron
        africa, Uganda
        africa, Kenya
        europe, Netherlands
}


dynamic_instance{
        root,city
        king, Seattle
        king, Redmond
        pierce, Tacoma
        pierce, Puyallup
        king_hyphen_texas, Dumont
        king_hyphen_texas, Finney
        cameron, brownsville
        cameron, harlingen
        uganda, Kampala
        uganda, Masaka
        uganda, Mbale
        uganda, Mbarara
        kenya, Nairobi
        kenya, Kisumu
        kenya, Eldoret
        netherlands, Netherlandis
        netherlands, Another Netherlands
}
"""
    ]

    void testAlign() {

        MarkupAligner formatter = new MarkupAligner(inpurt)

//        println formatter.align()
        assertEquals expected.trim(), formatter.align().trim()

    }

    void testDynamicInstance() {
        assertEquals markUp2.formatted, MarkupAligner.align(markUp2.raw)
    }

    void testMultiline() {
        println(MarkupAligner.align(mulitineMarkup2.raw))
        assertEquals mulitineMarkup2.formatted, MarkupAligner.align(mulitineMarkup2.raw)
    }
}
