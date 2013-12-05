package org.openxdata.markup.serializer

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 4/25/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
class NumberedXMLs {

    static def expectedXForm='''<xforms>
  <model>
    <instance id="snv_study_snv_form_v1">
      <snv_study_snv_form_v1 id="0" name="Snv Form" formKey="snv_study_snv_form_v1">
        <_1country />
        <_2district />
        <_3school />
        <_4how_are_you />
        <_5what_is_name />
        <_6what_is_sex />
        <_7select_your_diseases />
        <_8repeat_question_header>
          <_8_1rpt_question_1 />
          <_8_2rpt_question_2 />
        </_8repeat_question_header>
        <_9video />
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
    <bind id="_1country" nodeset="/snv_study_snv_form_v1/_1country" type="xsd:string" />
    <bind id="_2district" nodeset="/snv_study_snv_form_v1/_2district" type="xsd:string" required="true()" />
    <bind id="_3school" nodeset="/snv_study_snv_form_v1/_3school" type="xsd:string" />
    <bind id="_4how_are_you" nodeset="/snv_study_snv_form_v1/_4how_are_you" type="xsd:string" />
    <bind id="_5what_is_name" nodeset="/snv_study_snv_form_v1/_5what_is_name" type="xsd:string" />
    <bind id="_6what_is_sex" nodeset="/snv_study_snv_form_v1/_6what_is_sex" type="xsd:string" />
    <bind id="_7select_your_diseases" nodeset="/snv_study_snv_form_v1/_7select_your_diseases" type="xsd:string" />
    <bind id="_8repeat_question_header" nodeset="/snv_study_snv_form_v1/_8repeat_question_header" />
    <bind id="_8_1rpt_question_1" nodeset="/snv_study_snv_form_v1/_8repeat_question_header/_8_1rpt_question_1" type="xsd:string" />
    <bind id="_8_2rpt_question_2" nodeset="/snv_study_snv_form_v1/_8repeat_question_header/_8_2rpt_question_2" type="xsd:string" />
    <bind id="_9video" nodeset="/snv_study_snv_form_v1/_9video" type="xsd:base64Binary" format="video" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="_1country">
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
    <select1 bind="_2district">
      <label>2. District</label>
      <itemset nodeset="instance('district')/item[@parent=instance('snv_study_snv_form_v1')/_1country]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <select1 bind="_3school">
      <label>3. School</label>
      <itemset nodeset="instance('school')/item[@parent=instance('snv_study_snv_form_v1')/_2district]">
        <label ref="label" />
        <value ref="value" />
      </itemset>
    </select1>
    <input bind="_4how_are_you">
      <label>4. How are you</label>
    </input>
    <input bind="_5what_is_name">
      <label>5. what is name</label>
    </input>
    <select1 bind="_6what_is_sex">
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
    <select bind="_7select_your_diseases">
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
    <group id="_8repeat_question_header">
      <label>8. Repeat Question header</label>
      <repeat bind="_8repeat_question_header">
        <select1 bind="_8_1rpt_question_1">
          <label>8.1. rpt question 1</label>
          <item id="sdsd">
            <label>sdsd</label>
            <value>sdsd</value>
          </item>
        </select1>
        <select bind="_8_2rpt_question_2">
          <label>8.2. rpt question 2</label>
          <item id="dsksd">
            <label>dsksd</label>
            <value>dsksd</value>
          </item>
        </select>
      </repeat>
    </group>
    <upload bind="_9video" mediatype="video/*">
      <label>9. Video</label>
    </upload>
  </group>
</xforms>'''

    static def xformWithSkipLogicXML = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <_1sex />
        <_2pregnant />
        <_3male_question />
      </study_form_v1>
    </instance>
    <bind id="_1sex" nodeset="/study_form_v1/_1sex" type="xsd:string" />
    <bind id="_2pregnant" nodeset="/study_form_v1/_2pregnant" type="xsd:boolean" relevant="/study_form_v1/_1sex = 'female'" action="enable" />
    <bind id="_3male_question" nodeset="/study_form_v1/_3male_question" type="xsd:string" relevant="/study_form_v1/_1sex = 'male'" action="show" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="_1sex">
      <label>1. Sex</label>
      <item id="male">
        <label>Male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>Female</label>
        <value>female</value>
      </item>
    </select1>
    <input bind="_2pregnant">
      <label>2. Is pregnant</label>
    </input>
    <input bind="_3male_question">
      <label>3. Some male question</label>
    </input>
  </group>
</xforms>'''

    static def xfromWithValidationLogicXML ='''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <_1age />
        <_2age2 />
        <_3average />
      </study_form_v1>
    </instance>
    <bind id="_1age" nodeset="/study_form_v1/_1age" type="xsd:string" constraint=". &gt; 5" message="valid when greater than 5" />
    <bind id="_2age2" nodeset="/study_form_v1/_2age2" type="xsd:string" />
    <bind id="_3average" nodeset="/study_form_v1/_3average" type="xsd:string" calculate="(/study_form_v1/_1age + age2) div 2" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="_1age">
      <label>1. Age</label>
    </input>
    <input bind="_2age2">
      <label>2. Age2</label>
    </input>
    <input bind="_3average">
      <label>3. Average</label>
    </input>
  </group>
</xforms>'''

    static def xfromWithValidationLogicXMLUnNumberedBindings = '''<xforms>
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
      <label>1. Age</label>
    </input>
    <input bind="age2">
      <label>2. Age2</label>
    </input>
    <input bind="average">
      <label>3. Average</label>
    </input>
  </group>
</xforms>'''

    static def xmlWithRelativeBindings = '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="form" formKey="study_form_v1">
        <_1one />
        <_2two />
      </study_form_v1>
    </instance>
    <bind id="_1one" nodeset="/study_form_v1/_1one" type="xsd:string" />
    <bind id="_2two" nodeset="/study_form_v1/_2two" type="xsd:string" constraint=". = study_form_v1/_1one" message="blah" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="_1one">
      <label>1. One</label>
    </input>
    <input bind="_2two">
      <label>2. Two</label>
    </input>
  </group>
</xforms>'''


}
