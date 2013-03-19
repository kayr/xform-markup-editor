package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
class Fixtures {

    static def formMultiplePageDupeQuestion = '''### study

## form

#> Page1
 Age

 Sex

 #> Page2
 Name

 sex
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

    static def skipLogicInRepeat='''### Study

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

    static def normalPurcform = """###Snv Study
##Snv Form
>>>
Country,District,School
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
>>>

How are you
what is name

What is sex
>Male
>female

Select your diseases
>>AIDS
>>TB
>>Whooping cough

>>>>Repeat Question header
rpt question 1
>sdsd
rpt question 2
>>dsksd
>>>>

@video
Video


##form2
jeelopo
"""

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
      </dynamiclist>
    </instance>
    <bind id="country" nodeset="/snv_study_snv_form_v1/country" type="xsd:string" />
    <bind id="district" nodeset="/snv_study_snv_form_v1/district" type="xsd:string" />
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

    static def snvStudyXML = """<study name="Snv Study">
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
    &lt;bind id="district" nodeset="/snv_study_snv_form_v1/district" type="xsd:string" /&gt;
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
    </version>
  </form>
</study>"""

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
    <bind id="average" nodeset="/study_form_v1/average" type="xsd:decimal" calculate="(/study_form_v1/age + age2) div 2" />
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

}
