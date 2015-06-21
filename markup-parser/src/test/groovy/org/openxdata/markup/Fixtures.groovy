package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
class Fixtures {

    static String setFormDirectory() {
        def workingDir = System.getProperty('user.dir')
        def resourceFolder = "$workingDir/markup-parser/src/test/resources" //for ides
        if (!(new File(resourceFolder).exists()))
            resourceFolder = "$workingDir/src/test/resources"  //for maven
        System.setProperty("form.dir", resourceFolder)
        return resourceFolder
    }


    static def formWIthValue = '''### s

## f

@default Me
Some question
'''

    static def formWithDollarInString = '''### s

## s

@enableif . = '\\$eiwe $'
Q1

@enableif $q1 = '\\$ kdj' and '\\$q1' = 'someDigit'
Q2
'''

    static def formWithAbsoluteId = '''

### Study

## Form

question 1

@absoluteid mn
question 2

question 3
'''

    static def multipleForms =
            '''

### Study

## form

question

## form2

question

## form3

question

'''

    static def formWithEndtime = """
### Study

## Form

@id endtime
End time

Other question
"""

    static def oxdSampleForm = '''### Example study2

## Example form2

@longtext
Patient ID

@default mr
Title
>Mr
>Mrs

@longtext
First name

@required
Last name

Sex
>$male Male
>Female


@date
@validif . < today()
@message Birthdate Cannot be after today
Birthdate

@decimal
@validif . > 0  and . <= 200
@message Should be between 0 and 200 (inclusive)
Weight(Kg)

@decimal
@validif . >= 1 and . <= 9
@message Height should be between 1 and 9
Height

@enableif $sex = 'male'
Is patient pregnant

@comment Please select all anti-retrovirals that the patient is taking
ARVS
>>AZT
>>ABICVAR
>>EFIVARENCE
>>TRIOMUNE
>>TRUVADA

@picture
Picture

@audio
Sound

@video
Record video

dynamic{
Region,\tSub-Region,\t    City
Washington,\tKing,\t    Seattle
Washington,\tKing,\t    Redmond
Washington,\tPierce,\t    Tacoma
Washington,\tPierce,\t    Puyallup
Texas,\t    King-Texas,\tDumont
Texas,\t    King-Texas,\tFinney
Texas,\t    Cameron,\tbrownsville
Texas,\t    Cameron,\tharlingen
Africa,\t    Uganda,\t    Kampala
Africa,\t    Uganda,\t    Masaka
Africa,\t    Uganda,\t    Mbale
Africa,\t    Uganda,\t    Mbarara
Africa,\t    Kenya,\t    Nairobi
Africa,\t    Kenya,\t    Kisumu
Africa,\t    Kenya,\t    Eldoret
Europe,\t    Netherlands,Netherlandis
Europe,\t    Netherlands,Another Netherlands
}

@id children_number
Number of children



@validif length(.) =  $children_number
@message Enter details of all children
repeat{ Details of Children
    Name

    @number
    Age

    @id child_sex
    Sex
    >Male
    >Female
}



@time
Start time

@time
@id endtime
End time
'''
    static def formUsingRelativeBinds =
            '''
### Study

## form

One

@validif . = $:one
@message blah
Two
'''

    static def formWithCSVImport =
            '''
### study

## form

Quarter
>blah

csv:import quarters.csv
'''

    static def formWithDynamicInstanceReferences =
            '''
### study

## form

@id region
Select Region
>blah

@parent region
Subregion
$>subregion2

@parent region
Subregion dupe
$>subregion2

dynamic{
$region, subregion2
ug,kla
ug,mbra
ky,nai
ky,kis
}
'''

    static def formMultiplePageDupeQuestion = '''### study

## form

#> Page1
 Age

 Sex

 #> Page2
 Name

 sex
'''

    static def formWithValidationOnInnerRepeat =
            '''
### CountUnique Demo


## Drugs

Select Quarter
>Quarter 1
>Quarter 2

@validif length(.) = 8 and . = countunique(.,$drug_avalaible)
@message Please enter all 8 drugs
repeat{ Please check for the following drugs

        @id drug_avalaible
        *Drugs
        >Artemether/Lumefantrine (boxes)
        >SP(Fansidar)
        >IV Quinine
        >Oral Quinine
        >Artesunate( Rectal)
        >5% Dextrose
        >50% Dextrose
        >Artemether(Injectable)

        @number
        *Number on the  Stock Card


        @number
        *Actual number in the  Store
}
'''

    static def badSkipLogicInRepeat = '''### Study

## Form

Hello

repeat{Give the details for grading for the last PLE exam.
    @required
    @id grade_class
    @enableif $evaluation_period = 'quarter1_2012' or $evaluation_period = 'quarter1_2013'
    Class
      >Grade 1

}'''

    static def skipLogicInRepeat = '''### Study

## Form

Hello

repeat{Give the details for grading for the last PLE exam.
    @enableif $hello = true
    Class

}'''

    static def formRepeatWithAttributesOnRepeats = """### Study
## Form

@id child_repeat
@comment Details
repeat{ Children
  name
  sex
}
"""

    static def formWithMultiplePage = '''### Study

## Form

#> BioInfo

@id gender
Sex
>male
>female

@enableif  $gender = 'male'
Name

repeat{ Lol
 Child name
 Child Sex
}

#> Location

@enableif $name = 'peter'
Peters 2nd name

dynamic{
Country,District,School
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
}'''

    static def formWithDupePages = '''###Study
##Form
#>Bioform
  Name
  Sex
#>Bioform
    Country
'''

    static def formWithSkipLogic = '''### Study

## Form

@id sex
Sex
>Male
>Female

@skiplogic  $sex = 'female'
@id pregnant
Is pregnant

@skiplogic  $sex = 'male'
@id male_question
@skipaction  show
Some male question
'''

    static def formWithActionAttributes = '''###Study

## Form


@id sex
Sex
>Male
>Female

@enableif  $sex = 'female'
@id pregnant
Is pregnant

@showif  $sex = 'male'
@id male_question
Some male question
'''

    static def formWithValidationLogic = '''###Study
##Form

@validif . > 5
@message valid when greater than 5
@id age
Age

@id age2
Age2

@calculate ($age + age2) div 2
Average
'''

    static def formWithValidationLogicNoMessage = '''###Study
##Form

@validif . > 5
Age
'''

    static formWithErraticVariableSkipLogic = ''' ### Study

## Form

@id sex
Sex
>Male
>Female

@skiplogic  $seX = 'female'
@id pregnant
Is pregnant
'''

    static formWithErraticXPathSkipLogic = ''' ### Study

## Form

@id sex
Sex
>Male
>Female

@skiplogic  erratic xpath
@id pregnant
Is pregnant
'''

    static def formWithDuplicates = """### Study

## form

Hello

repeat{ Repeat header
     Hello
    }

    @id hello_last
    Hello
"""

    static def formRepeatChildDuplicates = '''
### Study

## form

repeat{ Repeat 1
     Hello
    }

    repeat{ Repeat 2
    Hello
    }


'''

    static def normalPurcform = """###Snv Study
##Snv Form
dynamic {
Country,*District,School
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
}

How are you
what is name

What is sex
>Male
>female

Select your diseases
>>AIDS
>>TB
>>Whooping cough

repeat { Repeat Question header
rpt question 1
>sdsd
rpt question 2
>>dsksd
}

@video
Video


##form2
jeelopo

//Deliberately put space here
    """

    static def formWithId =
            '''
### Study

@id form_v5
@dbId 97
## form

question
'''

    static def normalPurcform2 = '''###Snv Study
##Snv Form

@id country
Country
>null

@parent country
*District
$>district

@parent district
School
$>school

How are you
what is name

What is sex
>Male
>female

Select your diseases
>>AIDS
>>TB
>>Whooping cough

repeat { Repeat Question header
rpt question 1
>sdsd
rpt question 2
>>dsksd
}

@video
Video

dynamic {
$country,district,school
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
}
##form2
jeelopo
'''

    static def expectedXForm = '''<xforms>
  <model>
    <instance id="snv_study_snv_form_v1">
      <snv_study_snv_form_v1 id="0" name="Snv Form" formKey="snv_study_snv_form_v1">
        <country />
        <district />
        <school />
        <how_are_you />
        <what_is_name />
        <what_is_sex />
        <select_your_diseases />
        <repeat_question_header>
          <rpt_question_1 />
          <rpt_question_2 />
        </repeat_question_header>
        <video />
      </snv_study_snv_form_v1>
    </instance>
    <instance id="district">
      <dynamiclist>
        <item id="kampala" parent="uganda">
          <label>Kampala</label>
          <value>kampala</value>
        </item>
        <item id="nairobi" parent="kenya">
          <label>Nairobi</label>
          <value>nairobi</value>
        </item>
        <item id="kenya_kampala" parent="kenya">
          <label>Kampala</label>
          <value>kenya_kampala</value>
        </item>
      </dynamiclist>
    </instance>
    <instance id="school">
      <dynamiclist>
        <item id="macos" parent="kampala">
          <label>Macos</label>
          <value>macos</value>
        </item>
        <item id="machaccos" parent="nairobi">
          <label>Machaccos</label>
          <value>machaccos</value>
        </item>
        <item id="bugiroad" parent="kampala">
          <label>Bugiroad</label>
          <value>bugiroad</value>
        </item>
        <item id="kenya_kampala_bugiroad" parent="kenya_kampala">
          <label>Bugiroad</label>
          <value>kenya_kampala_bugiroad</value>
        </item>
      </dynamiclist>
    </instance>
    <bind id="country" nodeset="/snv_study_snv_form_v1/country" type="xsd:string" />
    <bind id="district" nodeset="/snv_study_snv_form_v1/district" type="xsd:string" required="true()" />
    <bind id="school" nodeset="/snv_study_snv_form_v1/school" type="xsd:string" />
    <bind id="how_are_you" nodeset="/snv_study_snv_form_v1/how_are_you" type="xsd:string" />
    <bind id="what_is_name" nodeset="/snv_study_snv_form_v1/what_is_name" type="xsd:string" />
    <bind id="what_is_sex" nodeset="/snv_study_snv_form_v1/what_is_sex" type="xsd:string" />
    <bind id="select_your_diseases" nodeset="/snv_study_snv_form_v1/select_your_diseases" type="xsd:string" />
    <bind id="repeat_question_header" nodeset="/snv_study_snv_form_v1/repeat_question_header" />
    <bind id="rpt_question_1" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_1" type="xsd:string" />
    <bind id="rpt_question_2" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_2" type="xsd:string" />
    <bind id="video" nodeset="/snv_study_snv_form_v1/video" type="xsd:base64Binary" format="video" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="country">
      <label>Country</label>
      <item id="uganda">
        <label>Uganda</label>
        <value>uganda</value>
      </item>
      <item id="kenya">
        <label>Kenya</label>
        <value>kenya</value>
      </item>
    </select1>
    <select1 bind="district">
      <label>District</label>
      <itemset nodeset="instance('district')/item[@parent=instance('snv_study_snv_form_v1')/country]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <select1 bind="school">
      <label>School</label>
      <itemset nodeset="instance('school')/item[@parent=instance('snv_study_snv_form_v1')/district]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <input bind="how_are_you">
      <label>How are you</label>
    </input>
    <input bind="what_is_name">
      <label>what is name</label>
    </input>
    <select1 bind="what_is_sex">
      <label>What is sex</label>
      <item id="male">
        <label>Male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>female</label>
        <value>female</value>
      </item>
    </select1>
    <select bind="select_your_diseases">
      <label>Select your diseases</label>
      <item id="aids">
        <label>AIDS</label>
        <value>aids</value>
      </item>
      <item id="tb">
        <label>TB</label>
        <value>tb</value>
      </item>
      <item id="whooping_cough">
        <label>Whooping cough</label>
        <value>whooping_cough</value>
      </item>
    </select>
    <group id="repeat_question_header">
      <label>Repeat Question header</label>
      <repeat bind="repeat_question_header">
        <select1 bind="rpt_question_1">
          <label>rpt question 1</label>
          <item id="sdsd">
            <label>sdsd</label>
            <value>sdsd</value>
          </item>
        </select1>
        <select bind="rpt_question_2">
          <label>rpt question 2</label>
          <item id="dsksd">
            <label>dsksd</label>
            <value>dsksd</value>
          </item>
        </select>
      </repeat>
    </group>
    <upload bind="video" mediatype="video/*">
      <label>Video</label>
    </upload>
  </group>
</xforms>'''
    static def expectedXFormWithNumberedLabels = '''<xforms>
  <model>
    <instance id="snv_study_snv_form_v1">
      <snv_study_snv_form_v1 id="0" name="Snv Form" formKey="snv_study_snv_form_v1">
        <country />
        <district />
        <school />
        <how_are_you />
        <what_is_name />
        <what_is_sex />
        <select_your_diseases />
        <repeat_question_header>
          <rpt_question_1 />
          <rpt_question_2 />
        </repeat_question_header>
        <video />
      </snv_study_snv_form_v1>
    </instance>
    <instance id="district">
      <dynamiclist>
        <item id="kampala" parent="uganda">
          <label>Kampala</label>
          <value>kampala</value>
        </item>
        <item id="nairobi" parent="kenya">
          <label>Nairobi</label>
          <value>nairobi</value>
        </item>
        <item id="kenya_kampala" parent="kenya">
          <label>Kampala</label>
          <value>kenya_kampala</value>
        </item>
      </dynamiclist>
    </instance>
    <instance id="school">
      <dynamiclist>
        <item id="macos" parent="kampala">
          <label>Macos</label>
          <value>macos</value>
        </item>
        <item id="machaccos" parent="nairobi">
          <label>Machaccos</label>
          <value>machaccos</value>
        </item>
        <item id="bugiroad" parent="kampala">
          <label>Bugiroad</label>
          <value>bugiroad</value>
        </item>
        <item id="kenya_kampala_bugiroad" parent="kenya_kampala">
          <label>Bugiroad</label>
          <value>kenya_kampala_bugiroad</value>
        </item>
      </dynamiclist>
    </instance>
    <bind id="country" nodeset="/snv_study_snv_form_v1/country" type="xsd:string" />
    <bind id="district" nodeset="/snv_study_snv_form_v1/district" type="xsd:string" required="true()" />
    <bind id="school" nodeset="/snv_study_snv_form_v1/school" type="xsd:string" />
    <bind id="how_are_you" nodeset="/snv_study_snv_form_v1/how_are_you" type="xsd:string" />
    <bind id="what_is_name" nodeset="/snv_study_snv_form_v1/what_is_name" type="xsd:string" />
    <bind id="what_is_sex" nodeset="/snv_study_snv_form_v1/what_is_sex" type="xsd:string" />
    <bind id="select_your_diseases" nodeset="/snv_study_snv_form_v1/select_your_diseases" type="xsd:string" />
    <bind id="repeat_question_header" nodeset="/snv_study_snv_form_v1/repeat_question_header" />
    <bind id="rpt_question_1" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_1" type="xsd:string" />
    <bind id="rpt_question_2" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_2" type="xsd:string" />
    <bind id="video" nodeset="/snv_study_snv_form_v1/video" type="xsd:base64Binary" format="video" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="country">
      <label>1. Country</label>
      <item id="uganda">
        <label>Uganda</label>
        <value>uganda</value>
      </item>
      <item id="kenya">
        <label>Kenya</label>
        <value>kenya</value>
      </item>
    </select1>
    <select1 bind="district">
      <label>2. District</label>
      <itemset nodeset="instance('district')/item[@parent=instance('snv_study_snv_form_v1')/country]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <select1 bind="school">
      <label>3. School</label>
      <itemset nodeset="instance('school')/item[@parent=instance('snv_study_snv_form_v1')/district]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <input bind="how_are_you">
      <label>4. How are you</label>
    </input>
    <input bind="what_is_name">
      <label>5. what is name</label>
    </input>
    <select1 bind="what_is_sex">
      <label>6. What is sex</label>
      <item id="male">
        <label>Male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>female</label>
        <value>female</value>
      </item>
    </select1>
    <select bind="select_your_diseases">
      <label>7. Select your diseases</label>
      <item id="aids">
        <label>AIDS</label>
        <value>aids</value>
      </item>
      <item id="tb">
        <label>TB</label>
        <value>tb</value>
      </item>
      <item id="whooping_cough">
        <label>Whooping cough</label>
        <value>whooping_cough</value>
      </item>
    </select>
    <group id="repeat_question_header">
      <label>8. Repeat Question header</label>
      <repeat bind="repeat_question_header">
        <select1 bind="rpt_question_1">
          <label>8.1. rpt question 1</label>
          <item id="sdsd">
            <label>sdsd</label>
            <value>sdsd</value>
          </item>
        </select1>
        <select bind="rpt_question_2">
          <label>8.2. rpt question 2</label>
          <item id="dsksd">
            <label>dsksd</label>
            <value>dsksd</value>
          </item>
        </select>
      </repeat>
    </group>
    <upload bind="video" mediatype="video/*">
      <label>9. Video</label>
    </upload>
  </group>
</xforms>'''

    static def snvStudyXML = '''<study name="Snv Study">
  <form name="Snv Form">
    <version name="v1">
      <xform>&lt;xforms&gt;
  &lt;model&gt;
    &lt;instance id="snv_study_snv_form_v1"&gt;
      &lt;snv_study_snv_form_v1 id="0" name="Snv Form" formKey="snv_study_snv_form_v1"&gt;
        &lt;country /&gt;
        &lt;district /&gt;
        &lt;school /&gt;
        &lt;how_are_you /&gt;
        &lt;what_is_name /&gt;
        &lt;what_is_sex /&gt;
        &lt;select_your_diseases /&gt;
        &lt;repeat_question_header&gt;
          &lt;rpt_question_1 /&gt;
          &lt;rpt_question_2 /&gt;
        &lt;/repeat_question_header&gt;
        &lt;video /&gt;
      &lt;/snv_study_snv_form_v1&gt;
    &lt;/instance&gt;
    &lt;instance id="district"&gt;
      &lt;dynamiclist&gt;
        &lt;item id="kampala" parent="uganda"&gt;
          &lt;label&gt;Kampala&lt;/label&gt;
          &lt;value&gt;kampala&lt;/value&gt;
        &lt;/item&gt;
        &lt;item id="nairobi" parent="kenya"&gt;
          &lt;label&gt;Nairobi&lt;/label&gt;
          &lt;value&gt;nairobi&lt;/value&gt;
        &lt;/item&gt;
      &lt;/dynamiclist&gt;
    &lt;/instance&gt;
    &lt;instance id="school"&gt;
      &lt;dynamiclist&gt;
        &lt;item id="macos" parent="kampala"&gt;
          &lt;label&gt;Macos&lt;/label&gt;
          &lt;value&gt;macos&lt;/value&gt;
        &lt;/item&gt;
        &lt;item id="machaccos" parent="nairobi"&gt;
          &lt;label&gt;Machaccos&lt;/label&gt;
          &lt;value&gt;machaccos&lt;/value&gt;
        &lt;/item&gt;
        &lt;item id="bugiroad" parent="kampala"&gt;
          &lt;label&gt;Bugiroad&lt;/label&gt;
          &lt;value&gt;bugiroad&lt;/value&gt;
        &lt;/item&gt;
      &lt;/dynamiclist&gt;
    &lt;/instance&gt;
    &lt;bind id="country" nodeset="/snv_study_snv_form_v1/country" type="xsd:string" /&gt;
    &lt;bind id="district" nodeset="/snv_study_snv_form_v1/district" type="xsd:string" required="true()" /&gt;
    &lt;bind id="school" nodeset="/snv_study_snv_form_v1/school" type="xsd:string" /&gt;
    &lt;bind id="how_are_you" nodeset="/snv_study_snv_form_v1/how_are_you" type="xsd:string" /&gt;
    &lt;bind id="what_is_name" nodeset="/snv_study_snv_form_v1/what_is_name" type="xsd:string" /&gt;
    &lt;bind id="what_is_sex" nodeset="/snv_study_snv_form_v1/what_is_sex" type="xsd:string" /&gt;
    &lt;bind id="select_your_diseases" nodeset="/snv_study_snv_form_v1/select_your_diseases" type="xsd:string" /&gt;
    &lt;bind id="repeat_question_header" nodeset="/snv_study_snv_form_v1/repeat_question_header" /&gt;
    &lt;bind id="rpt_question_1" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_1" type="xsd:string" /&gt;
    &lt;bind id="rpt_question_2" nodeset="/snv_study_snv_form_v1/repeat_question_header/rpt_question_2" type="xsd:string" /&gt;
    &lt;bind id="video" nodeset="/snv_study_snv_form_v1/video" type="xsd:base64Binary" format="video" /&gt;
  &lt;/model&gt;
  &lt;group id="1"&gt;
    &lt;label&gt;Page1&lt;/label&gt;
    &lt;select1 bind="country"&gt;
      &lt;label&gt;Country&lt;/label&gt;
      &lt;item id="uganda"&gt;
        &lt;label&gt;Uganda&lt;/label&gt;
        &lt;value&gt;uganda&lt;/value&gt;
      &lt;/item&gt;
      &lt;item id="kenya"&gt;
        &lt;label&gt;Kenya&lt;/label&gt;
        &lt;value&gt;kenya&lt;/value&gt;
      &lt;/item&gt;
    &lt;/select1&gt;
    &lt;select1 bind="district"&gt;
      &lt;label&gt;District&lt;/label&gt;
      &lt;itemset nodeset="instance('district')/item[@parent=instance('snv_study_snv_form_v1')/country]"&gt;
        &lt;label ref="label" /&gt;
        &lt;value ref="value" /&gt;
      &lt;/itemset&gt;
    &lt;/select1&gt;
    &lt;select1 bind="school"&gt;
      &lt;label&gt;School&lt;/label&gt;
      &lt;itemset nodeset="instance('school')/item[@parent=instance('snv_study_snv_form_v1')/district]"&gt;
        &lt;label ref="label" /&gt;
        &lt;value ref="value" /&gt;
      &lt;/itemset&gt;
    &lt;/select1&gt;
    &lt;input bind="how_are_you"&gt;
      &lt;label&gt;How are you&lt;/label&gt;
    &lt;/input&gt;
    &lt;input bind="what_is_name"&gt;
      &lt;label&gt;what is name&lt;/label&gt;
    &lt;/input&gt;
    &lt;select1 bind="what_is_sex"&gt;
      &lt;label&gt;What is sex&lt;/label&gt;
      &lt;item id="male"&gt;
        &lt;label&gt;Male&lt;/label&gt;
        &lt;value&gt;male&lt;/value&gt;
      &lt;/item&gt;
      &lt;item id="female"&gt;
        &lt;label&gt;female&lt;/label&gt;
        &lt;value&gt;female&lt;/value&gt;
      &lt;/item&gt;
    &lt;/select1&gt;
    &lt;select bind="select_your_diseases"&gt;
      &lt;label&gt;Select your diseases&lt;/label&gt;
      &lt;item id="aids"&gt;
        &lt;label&gt;AIDS&lt;/label&gt;
        &lt;value&gt;aids&lt;/value&gt;
      &lt;/item&gt;
      &lt;item id="tb"&gt;
        &lt;label&gt;TB&lt;/label&gt;
        &lt;value&gt;tb&lt;/value&gt;
      &lt;/item&gt;
      &lt;item id="whooping_cough"&gt;
        &lt;label&gt;Whooping cough&lt;/label&gt;
        &lt;value&gt;whooping_cough&lt;/value&gt;
      &lt;/item&gt;
    &lt;/select&gt;
    &lt;group id="repeat_question_header"&gt;
      &lt;label&gt;Repeat Question header&lt;/label&gt;
      &lt;repeat bind="repeat_question_header"&gt;
        &lt;select1 bind="rpt_question_1"&gt;
          &lt;label&gt;rpt question 1&lt;/label&gt;
          &lt;item id="sdsd"&gt;
            &lt;label&gt;sdsd&lt;/label&gt;
            &lt;value&gt;sdsd&lt;/value&gt;
          &lt;/item&gt;
        &lt;/select1&gt;
        &lt;select bind="rpt_question_2"&gt;
          &lt;label&gt;rpt question 2&lt;/label&gt;
          &lt;item id="dsksd"&gt;
            &lt;label&gt;dsksd&lt;/label&gt;
            &lt;value&gt;dsksd&lt;/value&gt;
          &lt;/item&gt;
        &lt;/select&gt;
      &lt;/repeat&gt;
    &lt;/group&gt;
    &lt;upload bind="video" mediatype="video/*"&gt;
      &lt;label&gt;Video&lt;/label&gt;
    &lt;/upload&gt;
  &lt;/group&gt;
&lt;/xforms&gt;</xform>
      <layout>&lt;Form&gt;
  &lt;Page Text="Page1" fontWeight="normal" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Binding="" Width="1600px" Height="1032px" backgroundColor=""&gt;
    &lt;Item WidgetType="Label" Text="Country" HelpText="" Binding="country" Left="20px" Top="20px" Width="56px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="ListBox" Text="" HelpText="Country" Binding="country" Left="40px" Top="37px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="District" HelpText="" Binding="district" Left="20px" Top="82px" Width="64px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="ListBox" Text="" HelpText="District" Binding="district" Left="40px" Top="99px" Width="200px" Height="25px" TabIndex="1" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="School" HelpText="" Binding="school" Left="20px" Top="144px" Width="48px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="ListBox" Text="" HelpText="School" Binding="school" Left="40px" Top="161px" Width="200px" Height="25px" TabIndex="2" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="How are you" HelpText="" Binding="how_are_you" Left="20px" Top="206px" Width="88px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="TextBox" Text="" HelpText="How are you" Binding="how_are_you" Left="40px" Top="223px" Width="200px" Height="25px" TabIndex="3" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="what is name" HelpText="" Binding="what_is_name" Left="20px" Top="268px" Width="96px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="TextBox" Text="" HelpText="what is name" Binding="what_is_name" Left="40px" Top="285px" Width="200px" Height="25px" TabIndex="4" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="What is sex" HelpText="" Binding="what_is_sex" Left="20px" Top="330px" Width="88px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="ListBox" Text="" HelpText="What is sex" Binding="what_is_sex" Left="40px" Top="347px" Width="200px" Height="25px" TabIndex="5" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="Select your diseases" HelpText="" Binding="select_your_diseases" Left="20px" Top="392px" Width="160px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="CheckBox" Text="AIDS" HelpText="AIDS" Binding="aids" ParentBinding="select_your_diseases" Left="40px" Top="412px" Width="200px" Height="25px" TabIndex="6" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="CheckBox" Text="TB" HelpText="TB" Binding="tb" ParentBinding="select_your_diseases" Left="40px" Top="432px" Width="200px" Height="25px" TabIndex="7" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="CheckBox" Text="Whooping cough" HelpText="Whooping cough" Binding="whooping_cough" ParentBinding="select_your_diseases" Left="40px" Top="452px" Width="200px" Height="25px" TabIndex="8" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="Repeat Question header" HelpText="" Binding="repeat_question_header" Left="20px" Top="507px" Width="176px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontWeight="bold" fontStyle="italic" /&gt;
    &lt;Item WidgetType="Label" Text="rpt question 1" HelpText="" Binding="rpt_question_1" Left="0px" Top="532px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Label" Text="rpt question 2" HelpText="" Binding="rpt_question_2" Left="200px" Top="532px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="GroupBox" HelpText="Repeat Question header" Binding="repeat_question_header" Left="0px" Top="557px" Width="400px" Height="100px" TabIndex="9" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Repeated="1"&gt;
      &lt;Item WidgetType="Button" Text="Add New" HelpText="addnew" Binding="addnew" Left="10px" Top="55px" Width="90px" Height="30px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
      &lt;Item WidgetType="ListBox" Text="" HelpText="rpt question 1" Binding="rpt_question_1" Left="0px" Top="10px" Width="200px" Height="25px" TabIndex="1" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
      &lt;Item WidgetType="TextBox" Text="" HelpText="rpt question 2" Binding="rpt_question_2" Left="200px" Top="10px" Width="200px" Height="25px" TabIndex="2" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;/Item&gt;
    &lt;Item WidgetType="GroupBox" HelpText="Video" Binding="LEFT20pxTOP687px" Left="40px" Top="687px" Width="200px" Height="125px" TabIndex="10" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" borderStyle="dashed"&gt;
      &lt;Item WidgetType="VideoAudio" Text="Click to play" Binding="video" Left="45px" Top="45px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
      &lt;Item WidgetType="Button" Text="Browse" HelpText="browse" Binding="browse" ParentBinding="video" Left="10px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
      &lt;Item WidgetType="Button" Text="Clear" HelpText="clear" Binding="clear" ParentBinding="video" Left="120px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
      &lt;Item WidgetType="Label" Text="Video" Binding="LEFT20pxTOP687px" Left="0px" Top="0px" Width="100%" Height="20px" TabIndex="0" color="white" fontWeight="bold" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif" backgroundColor="rgb(143, 171, 199)" textAlign="center" HeaderLabel="true" /&gt;
    &lt;/Item&gt;
    &lt;Item WidgetType="Button" Text="Submit" HelpText="submit" Binding="submit" Left="20px" Top="832px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Button" Text="Cancel" HelpText="cancel" Binding="cancel" Left="220px" Top="832px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
  &lt;/Page&gt;
&lt;/Form&gt;</layout>
    </version>
  </form>
  <form name="form2">
    <version name="v1">
      <xform>&lt;xforms&gt;
  &lt;model&gt;
    &lt;instance id="snv_study_form2_v1"&gt;
      &lt;snv_study_form2_v1 id="0" name="form2" formKey="snv_study_form2_v1"&gt;
        &lt;jeelopo /&gt;
      &lt;/snv_study_form2_v1&gt;
    &lt;/instance&gt;
    &lt;bind id="jeelopo" nodeset="/snv_study_form2_v1/jeelopo" type="xsd:string" /&gt;
  &lt;/model&gt;
  &lt;group id="1"&gt;
    &lt;label&gt;Page1&lt;/label&gt;
    &lt;input bind="jeelopo"&gt;
      &lt;label&gt;jeelopo&lt;/label&gt;
    &lt;/input&gt;
  &lt;/group&gt;
&lt;/xforms&gt;</xform>
      <layout>&lt;Form&gt;
  &lt;Page Text="Page1" fontWeight="normal" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Binding="" Width="1600px" Height="282px" backgroundColor=""&gt;
    &lt;Item WidgetType="Label" Text="jeelopo" HelpText="" Binding="jeelopo" Left="20px" Top="20px" Width="56px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="TextBox" Text="" HelpText="jeelopo" Binding="jeelopo" Left="40px" Top="37px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Button" Text="Submit" HelpText="submit" Binding="submit" Left="20px" Top="82px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
    &lt;Item WidgetType="Button" Text="Cancel" HelpText="cancel" Binding="cancel" Left="220px" Top="82px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" /&gt;
  &lt;/Page&gt;
&lt;/Form&gt;</layout>
    </version>
  </form>
</study>'''

    static def xformWithAttribsXML = """<xforms>
  <model>
    <instance id="study_name_form_v1">
      <study_name_form_v1 id="0" name="Form" formKey="study_name_form_v1">
        <age_of_boy />
        <height />
        <take_picture />
        <weight />
        <hkdksdj />
        <testing_id />
      </study_name_form_v1>
    </instance>
    <bind id="age_of_boy" nodeset="/study_name_form_v1/age_of_boy" type="xsd:int" />
    <bind id="height" nodeset="/study_name_form_v1/height" type="xsd:decimal" />
    <bind id="take_picture" nodeset="/study_name_form_v1/take_picture" type="xsd:base64Binary" format="video" />
    <bind id="weight" nodeset="/study_name_form_v1/weight" type="xsd:decimal" />
    <bind id="hkdksdj" nodeset="/study_name_form_v1/hkdksdj" type="xsd:string" required="true()" />
    <bind id="testing_id" nodeset="/study_name_form_v1/testing_id" type="xsd:string" required="true()" visible="false()" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="age_of_boy">
      <label>Age</label>
    </input>
    <input bind="height">
      <label>height</label>
    </input>
    <upload bind="take_picture" mediatype="video/*">
      <label>Take picture</label>
    </upload>
    <input bind="weight">
      <label>weight</label>
    </input>
    <select1 bind="hkdksdj">
      <label>Hkdksdj</label>
      <item id="dsk">
        <label>dsk</label>
        <value>dsk</value>
      </item>
      <item id="sdod">
        <label>sdod</label>
        <value>sdod</value>
      </item>
    </select1>
    <select bind="testing_id">
      <label>Jjidjf</label>
      <item id="fkldf">
        <label>fkldf</label>
        <value>fkldf</value>
      </item>
      <item id="kkdflf">
        <label>kkdflf</label>
        <value>kkdflf</value>
      </item>
    </select>
  </group>
</xforms>"""

    static def formWithAttribs = """###Study name
##Form

@id age_of_boy
@number
Age

@decimal
height

@video
Take picture

@decimal
weight

@required
Hkdksdj
>dsk
>sdod

@id testing_id
@required
@invisible
Jjidjf
>>fkldf
>>kkdflf
"""

    static def requiredQns = '''

### Study

## Form

@required
Hello

@required
Ait
>ds

@required
Hello2
>>re

@required
repeat{ Required repeat
    Jon
}

'''

    static def requiredTwo = '''

### Study

## Form


*Hello


*Ait
>ds


*Hello2
>>re


repeat{ *Required repeat
    Jon
}

'''

    static def xformWithSkipLogicXML = """<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <sex />
        <pregnant />
        <male_question />
      </study_form_v1>
    </instance>
    <bind id="sex" nodeset="/study_form_v1/sex" type="xsd:string" />
    <bind id="pregnant" nodeset="/study_form_v1/pregnant" type="xsd:boolean" relevant="/study_form_v1/sex = 'female'" action="enable" />
    <bind id="male_question" nodeset="/study_form_v1/male_question" type="xsd:string" relevant="/study_form_v1/sex = 'male'" action="show" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="sex">
      <label>Sex</label>
      <item id="male">
        <label>Male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>Female</label>
        <value>female</value>
      </item>
    </select1>
    <input bind="pregnant">
      <label>Is pregnant</label>
    </input>
    <input bind="male_question">
      <label>Some male question</label>
    </input>
  </group>
</xforms>"""

    static def xfromWithValidationLogicXML = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <age />
        <age2 />
        <average />
      </study_form_v1>
    </instance>
    <bind id="age" nodeset="/study_form_v1/age" type="xsd:string" constraint=". &gt; 5" message="valid when greater than 5" />
    <bind id="age2" nodeset="/study_form_v1/age2" type="xsd:string" />
    <bind id="average" nodeset="/study_form_v1/average" type="xsd:string" calculate="(/study_form_v1/age + age2) div 2" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="age">
      <label>Age</label>
    </input>
    <input bind="age2">
      <label>Age2</label>
    </input>
    <input bind="average">
      <label>Average</label>
    </input>
  </group>
</xforms>'''

    static def xformWithRepeatAttributesXML = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <child_repeat>
          <name />
          <sex />
        </child_repeat>
      </study_form_v1>
    </instance>
    <bind id="child_repeat" nodeset="/study_form_v1/child_repeat" />
    <bind id="name" nodeset="/study_form_v1/child_repeat/name" type="xsd:string" />
    <bind id="sex" nodeset="/study_form_v1/child_repeat/sex" type="xsd:string" />
  </model>
  <group id="1">
    <label>Page1</label>
    <group id="child_repeat">
      <label>Children</label>
      <hint>Details</hint>
      <repeat bind="child_repeat">
        <input bind="name">
          <label>name</label>
        </input>
        <input bind="sex">
          <label>sex</label>
        </input>
      </repeat>
    </group>
  </group>
</xforms>'''

    static def formWithMultiPageXML = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <gender />
        <name />
        <lol>
          <child_name />
          <child_sex />
        </lol>
        <peters_2nd_name />
        <country />
        <district />
        <school />
      </study_form_v1>
    </instance>
    <instance id="district">
      <dynamiclist>
        <item id="kampala" parent="uganda">
          <label>Kampala</label>
          <value>kampala</value>
        </item>
        <item id="nairobi" parent="kenya">
          <label>Nairobi</label>
          <value>nairobi</value>
        </item>
        <item id="kenya_kampala" parent="kenya">
          <label>Kampala</label>
          <value>kenya_kampala</value>
        </item>
      </dynamiclist>
    </instance>
    <instance id="school">
      <dynamiclist>
        <item id="macos" parent="kampala">
          <label>Macos</label>
          <value>macos</value>
        </item>
        <item id="machaccos" parent="nairobi">
          <label>Machaccos</label>
          <value>machaccos</value>
        </item>
        <item id="bugiroad" parent="kampala">
          <label>Bugiroad</label>
          <value>bugiroad</value>
        </item>
        <item id="kenya_kampala_bugiroad" parent="kenya_kampala">
          <label>Bugiroad</label>
          <value>kenya_kampala_bugiroad</value>
        </item>
      </dynamiclist>
    </instance>
    <bind id="gender" nodeset="/study_form_v1/gender" type="xsd:string" />
    <bind id="name" nodeset="/study_form_v1/name" type="xsd:string" relevant="/study_form_v1/gender = 'male'" action="enable" />
    <bind id="lol" nodeset="/study_form_v1/lol" />
    <bind id="child_name" nodeset="/study_form_v1/lol/child_name" type="xsd:string" />
    <bind id="child_sex" nodeset="/study_form_v1/lol/child_sex" type="xsd:string" />
    <bind id="peters_2nd_name" nodeset="/study_form_v1/peters_2nd_name" type="xsd:string" relevant="/study_form_v1/name = 'peter'" action="enable" />
    <bind id="country" nodeset="/study_form_v1/country" type="xsd:string" />
    <bind id="district" nodeset="/study_form_v1/district" type="xsd:string" />
    <bind id="school" nodeset="/study_form_v1/school" type="xsd:string" />
  </model>
  <group id="1">
    <label>BioInfo</label>
    <select1 bind="gender">
      <label>Sex</label>
      <item id="male">
        <label>male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>female</label>
        <value>female</value>
      </item>
    </select1>
    <input bind="name">
      <label>Name</label>
    </input>
    <group id="lol">
      <label>Lol</label>
      <repeat bind="lol">
        <input bind="child_name">
          <label>Child name</label>
        </input>
        <input bind="child_sex">
          <label>Child Sex</label>
        </input>
      </repeat>
    </group>
  </group>
  <group id="2">
    <label>Location</label>
    <input bind="peters_2nd_name">
      <label>Peters 2nd name</label>
    </input>
    <select1 bind="country">
      <label>Country</label>
      <item id="uganda">
        <label>Uganda</label>
        <value>uganda</value>
      </item>
      <item id="kenya">
        <label>Kenya</label>
        <value>kenya</value>
      </item>
    </select1>
    <select1 bind="district">
      <label>District</label>
      <itemset nodeset="instance('district')/item[@parent=instance('study_form_v1')/country]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <select1 bind="school">
      <label>School</label>
      <itemset nodeset="instance('school')/item[@parent=instance('study_form_v1')/district]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
  </group>
</xforms>'''

    static def xmlFormWithId =
            '''<xforms>
  <model>
    <instance id="form_v5">
      <form_v5 id="97" name="form" formKey="form_v5">
        <question />
      </form_v5>
    </instance>
    <bind id="question" nodeset="/form_v5/question" type="xsd:string" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="question">
      <label>question</label>
    </input>
  </group>
</xforms>'''

    static def xmlFormWithDynamicInstanceIds =
            '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="form" formKey="study_form_v1">
        <region />
        <subregion />
        <subregion_dupe />
      </study_form_v1>
    </instance>
    <instance id="subregion2">
      <dynamiclist>
        <item id="kla" parent="ug">
          <label>kla</label>
          <value>kla</value>
        </item>
        <item id="mbra" parent="ug">
          <label>mbra</label>
          <value>mbra</value>
        </item>
        <item id="nai" parent="ky">
          <label>nai</label>
          <value>nai</value>
        </item>
        <item id="kis" parent="ky">
          <label>kis</label>
          <value>kis</value>
        </item>
      </dynamiclist>
    </instance>
    <bind id="region" nodeset="/study_form_v1/region" type="xsd:string" />
    <bind id="subregion" nodeset="/study_form_v1/subregion" type="xsd:string" />
    <bind id="subregion_dupe" nodeset="/study_form_v1/subregion_dupe" type="xsd:string" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="region">
      <label>Select Region</label>
      <item id="ug">
        <label>ug</label>
        <value>ug</value>
      </item>
      <item id="ky">
        <label>ky</label>
        <value>ky</value>
      </item>
    </select1>
    <select1 bind="subregion">
      <label>Subregion</label>
      <itemset nodeset="instance('subregion2')/item[@parent=instance('study_form_v1')/region]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <select1 bind="subregion_dupe">
      <label>Subregion dupe</label>
      <itemset nodeset="instance('subregion2')/item[@parent=instance('study_form_v1')/region]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
  </group>
</xforms>'''

    static def xmlWithRelativeBindings = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="form" formKey="study_form_v1">
        <one />
        <two />
      </study_form_v1>
    </instance>
    <bind id="one" nodeset="/study_form_v1/one" type="xsd:string" />
    <bind id="two" nodeset="/study_form_v1/two" type="xsd:string" constraint=". = study_form_v1/one" message="blah" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="one">
      <label>One</label>
    </input>
    <input bind="two">
      <label>Two</label>
    </input>
  </group>
</xforms>'''


    static def xmlOxdSampleForm = '''<Form>
  <Page Text="Page1" fontWeight="normal" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Binding="" Width="1600px" Height="2216px" backgroundColor="">
    <Item Top="20px" Text="Patient ID" TabIndex="0" Height="25px" Width="80px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="patient_id" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextArea" Text="" HelpText="Patient ID" Binding="patient_id" Top="37px" Width="250px" Height="100px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="165px" Text="Title" TabIndex="0" Height="25px" Width="40px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="title" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="Title" Binding="title" Top="182px" Width="200px" Height="25px" TabIndex="1" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="227px" Text="First name" TabIndex="0" Height="25px" Width="80px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="first_name" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextArea" Text="" HelpText="First name" Binding="first_name" Top="244px" Width="250px" Height="100px" TabIndex="2" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="372px" Text="Last name" TabIndex="0" Height="25px" Width="72px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="last_name" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextBox" Text="" HelpText="Last name" Binding="last_name" Top="389px" Width="200px" Height="25px" TabIndex="3" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="434px" Text="Sex" TabIndex="0" Height="25px" Width="24px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="sex" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="Sex" Binding="sex" Top="451px" Width="200px" Height="25px" TabIndex="4" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="496px" Text="Birthdate" TabIndex="0" Height="25px" Width="72px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="birthdate" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="DatePicker" Text="" HelpText="Birthdate" Binding="birthdate" Top="513px" Width="200px" Height="25px" TabIndex="5" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="558px" Text="Weight(Kg)" TabIndex="0" Height="25px" Width="80px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="weightkg" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextBox" Text="" HelpText="Weight(Kg)" Binding="weightkg" Top="575px" Width="200px" Height="25px" TabIndex="6" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="620px" Text="Height" TabIndex="0" Height="25px" Width="48px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="height" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextBox" Text="" HelpText="Height" Binding="height" Top="637px" Width="200px" Height="25px" TabIndex="7" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="682px" Text="Is patient pregnant" TabIndex="0" Height="25px" Width="152px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="is_patient_pregnant" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="Is patient pregnant" Binding="is_patient_pregnant" Top="699px" Width="200px" Height="25px" TabIndex="8" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="744px" Text="ARVS" TabIndex="0" Height="25px" Width="32px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="arvs" Left="20px" HelpText="Please select all anti-retrovirals that the patient is taking" WidgetType="Label" />
    <Item WidgetType="CheckBox" Text="AZT" HelpText="AZT" Binding="azt" ParentBinding="arvs" Top="764px" Width="700px" Height="25px" TabIndex="9" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="CheckBox" Text="ABICVAR" HelpText="ABICVAR" Binding="abicvar" ParentBinding="arvs" Top="784px" Width="700px" Height="25px" TabIndex="10" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="CheckBox" Text="EFIVARENCE" HelpText="EFIVARENCE" Binding="efivarence" ParentBinding="arvs" Top="804px" Width="700px" Height="25px" TabIndex="11" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="CheckBox" Text="TRIOMUNE" HelpText="TRIOMUNE" Binding="triomune" ParentBinding="arvs" Top="824px" Width="700px" Height="25px" TabIndex="12" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="CheckBox" Text="TRUVADA" HelpText="TRUVADA" Binding="truvada" ParentBinding="arvs" Top="844px" Width="700px" Height="25px" TabIndex="13" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="GroupBox" HelpText="Video" Binding="LEFT20pxTOP909px" Left="40px" Top="909px" Width="200px" Height="245px" TabIndex="14" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" borderStyle="dashed">
      <Item WidgetType="Picture" Text="Click to play" Binding="picture" Left="10px" Top="35px" Width="185px" Height="155px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Browse" HelpText="browse" Binding="browse" ParentBinding="picture" Left="10px" Top="200px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Clear" HelpText="clear" Binding="clear" ParentBinding="picture" Left="120px" Top="200px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Label" Text="Picture" Binding="LEFT20pxTOP909px" Left="0px" Top="0px" Width="100%" Height="20px" TabIndex="0" color="white" fontWeight="bold" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif" backgroundColor="rgb(143, 171, 199)" textAlign="center" HeaderLabel="true" />
    </Item>
    <Item WidgetType="GroupBox" HelpText="Video" Binding="LEFT20pxTOP1174px" Left="40px" Top="1174px" Width="200px" Height="125px" TabIndex="15" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" borderStyle="dashed">
      <Item WidgetType="VideoAudio" Text="Click to play" Binding="sound" Left="45px" Top="45px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Browse" HelpText="browse" Binding="browse" ParentBinding="sound" Left="10px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Clear" HelpText="clear" Binding="clear" ParentBinding="sound" Left="120px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Label" Text="Sound" Binding="LEFT20pxTOP1174px" Left="0px" Top="0px" Width="100%" Height="20px" TabIndex="0" color="white" fontWeight="bold" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif" backgroundColor="rgb(143, 171, 199)" textAlign="center" HeaderLabel="true" />
    </Item>
    <Item WidgetType="GroupBox" HelpText="Video" Binding="LEFT20pxTOP1319px" Left="40px" Top="1319px" Width="200px" Height="125px" TabIndex="16" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" borderStyle="dashed">
      <Item WidgetType="VideoAudio" Text="Click to play" Binding="record_video" Left="45px" Top="45px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Browse" HelpText="browse" Binding="browse" ParentBinding="record_video" Left="10px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Button" Text="Clear" HelpText="clear" Binding="clear" ParentBinding="record_video" Left="120px" Top="85px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="Label" Text="Record video" Binding="LEFT20pxTOP1319px" Left="0px" Top="0px" Width="100%" Height="20px" TabIndex="0" color="white" fontWeight="bold" fontSize="16px" fontFamily="Verdana,'Lucida Grande','Trebuchet MS',Arial,Sans-Serif" backgroundColor="rgb(143, 171, 199)" textAlign="center" HeaderLabel="true" />
    </Item>
    <Item Top="1464px" Text="Region" TabIndex="0" Height="25px" Width="48px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="region" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="Region" Binding="region" Top="1481px" Width="200px" Height="25px" TabIndex="17" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="1526px" Text="Sub-Region" TabIndex="0" Height="25px" Width="80px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="sub_hyphen_region" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="Sub-Region" Binding="sub_hyphen_region" Top="1543px" Width="200px" Height="25px" TabIndex="18" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="1588px" Text="City" TabIndex="0" Height="25px" Width="32px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="city" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="ListBox" Text="" HelpText="City" Binding="city" Top="1605px" Width="200px" Height="25px" TabIndex="19" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="1650px" Text="Number of children" TabIndex="0" Height="25px" Width="144px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="children_number" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TextBox" Text="" HelpText="Number of children" Binding="children_number" Top="1667px" Width="200px" Height="25px" TabIndex="20" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="1712px" TabIndex="0" Height="25px" fontStyle="italic" fontSize="12px" Left="20px" HelpText="" fontWeight="bold" Text="Details of Children" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Width="152px" Binding="details_of_children" WidgetType="Label" />
    <Item WidgetType="Label" Text="Name" HelpText="Name" Binding="name" Left="0px" Top="1737px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
    <Item WidgetType="Label" Text="Age" HelpText="Age" Binding="age" Left="200px" Top="1737px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
    <Item WidgetType="Label" Text="Sex" HelpText="Sex" Binding="child_sex" Left="400px" Top="1737px" Width="200px" Height="25px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
    <Item WidgetType="GroupBox" HelpText="Details of Children" Binding="details_of_children" Left="0px" Top="1762px" Width="600px" Height="100px" TabIndex="21" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Repeated="1">
      <Item WidgetType="Button" Text="Add New" HelpText="addnew" Binding="addnew" Left="10px" Top="55px" Width="90px" Height="30px" TabIndex="0" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="TextBox" Text="" HelpText="Name" Binding="name" Left="0px" Top="10px" Width="200px" Height="25px" TabIndex="1" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="TextBox" Text="" HelpText="Age" Binding="age" Left="200px" Top="10px" Width="200px" Height="25px" TabIndex="2" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
      <Item WidgetType="ListBox" Text="" HelpText="Sex" Binding="child_sex" Left="400px" Top="10px" Width="200px" Height="25px" TabIndex="3" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
    </Item>
    <Item Top="1892px" Text="Start time" TabIndex="0" Height="25px" Width="80px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="start_time" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TimeWidget" Text="" HelpText="Start time" Binding="start_time" Top="1909px" Width="200px" Height="25px" TabIndex="22" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item Top="1954px" Text="End time" TabIndex="0" Height="25px" Width="64px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" fontSize="12px" Binding="endtime" Left="20px" HelpText="" WidgetType="Label" />
    <Item WidgetType="TimeWidget" Text="" HelpText="End time" Binding="endtime" Top="1971px" Width="200px" Height="25px" TabIndex="23" fontSize="12px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" Left="40px" />
    <Item WidgetType="Button" Text="Submit" HelpText="submit" Binding="submit" Left="20px" Top="2016px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
    <Item WidgetType="Button" Text="Cancel" HelpText="cancel" Binding="cancel" Left="220px" Top="2016px" Width="90px" Height="30px" TabIndex="0" fontSize="16px" fontFamily="Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif" />
  </Page>
</Form>'''

    def static absoluteIdXML = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <_1question_1 />
        <mn />
        <_3question_3 />
      </study_form_v1>
    </instance>
    <bind id="_1question_1" nodeset="/study_form_v1/_1question_1" type="xsd:string" />
    <bind id="mn" nodeset="/study_form_v1/mn" type="xsd:string" />
    <bind id="_3question_3" nodeset="/study_form_v1/_3question_3" type="xsd:string" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="_1question_1">
      <label>1. question 1</label>
    </input>
    <input bind="mn">
      <label>2. question 2</label>
    </input>
    <input bind="_3question_3">
      <label>3. question 3</label>
    </input>
  </group>
</xforms>'''
}
