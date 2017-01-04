package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures

/**
 * Created by kay on 7/15/14.
 */
class ODKFixtures {

    static def formWithInvisible = [ //tests that element with only invisible elements does not render body
            form: '''
### Std
## Frm

@invisible
Invisible


@readonly
Disabled

''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Frm</h:title>
    <model>
      <instance>
        <std_frm_v1 id="0" name="Frm">
          <invisible />
          <disabled />
        </std_frm_v1>
      </instance>
      <bind id="invisible" nodeset="/std_frm_v1/invisible" type="string" />
      <bind id="disabled" nodeset="/std_frm_v1/disabled" type="string" readonly="true()" />
    </model>
  </h:head>
  <h:body>
    <input ref="/std_frm_v1/disabled">
      <label>Disabled</label>
    </input>
  </h:body>
</h:html>'''
    ]
    static def formWithSkipLogicAndReadOnly = [
            form: '''
### Std
## Frm

Sex
>m
>f

@readonly
@enableif $sex = 'f'
Is pregnant

@readonly
@hideif $sex = 'f'
Are you a father HideIf

@readonly
@disableif $sex = 'f'
Are you a father DisableIF
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Frm</h:title>
    <model>
      <instance>
        <std_frm_v1 id="0" name="Frm">
          <sex />
          <is_pregnant />
          <are_you_a_father_hideif />
          <are_you_a_father_disableif />
        </std_frm_v1>
      </instance>
      <bind id="sex" nodeset="/std_frm_v1/sex" type="string" />
      <bind id="is_pregnant" nodeset="/std_frm_v1/is_pregnant" type="string" relevant="/std_frm_v1/sex = 'f'" />
      <bind id="are_you_a_father_hideif" nodeset="/std_frm_v1/are_you_a_father_hideif" type="string" readonly="true()" relevant="not(/std_frm_v1/sex = 'f')" />
      <bind id="are_you_a_father_disableif" nodeset="/std_frm_v1/are_you_a_father_disableif" type="string" readonly="true()" relevant="not(/std_frm_v1/sex = 'f')" />
    </model>
  </h:head>
  <h:body>
    <select1 ref="/std_frm_v1/sex">
      <label>Sex</label>
      <item>
        <label>m</label>
        <value>m</value>
      </item>
      <item>
        <label>f</label>
        <value>f</value>
      </item>
    </select1>
    <select1 ref="/std_frm_v1/is_pregnant">
      <label>Is pregnant</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <select1 ref="/std_frm_v1/are_you_a_father_hideif">
      <label>Are you a father HideIf</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <select1 ref="/std_frm_v1/are_you_a_father_disableif">
      <label>Are you a father DisableIF</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
  </h:body>
</h:html>'''
    ]

    static def timeStamp = [
            form: '''### S
## F

@id endtime
@dateTime
Prefilled Date
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>F</h:title>
    <model>
      <instance>
        <s_f_v1 id="0" name="F">
          <endtime />
        </s_f_v1>
      </instance>
      <bind id="endtime" nodeset="/s_f_v1/endtime" type="dateTime" jr:preload="timestamp" jr:preloadParams="end" />
    </model>
  </h:head>
  <h:body>
    <input ref="/s_f_v1/endtime">
      <label>Prefilled Date</label>
    </input>
  </h:body>
</h:html>'''
    ]

    static def oxdSampleForm = [
            form: Fixtures.oxdSampleForm,
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Example form2</h:title>
    <model>
      <instance>
        <example_study2_example_form2_v1 id="0" name="Example form2">
          <patient_id />
          <title>mr</title>
          <first_name />
          <last_name />
          <sex />
          <birthdate />
          <weightkg />
          <height />
          <is_patient_pregnant />
          <arvs />
          <picture />
          <sound />
          <record_video />
          <region />
          <sub_hyphen_region />
          <city />
          <children_number />
          <details_of_children>
            <name />
            <age />
            <child_sex />
          </details_of_children>
          <start_time />
          <endtime />
        </example_study2_example_form2_v1>
      </instance>
      <instance id="sub_hyphen_region">
        <dynamiclist>
          <item id="king" parent="washington">
            <label>King</label>
            <value>king</value>
          </item>
          <item id="pierce" parent="washington">
            <label>Pierce</label>
            <value>pierce</value>
          </item>
          <item id="king_hyphen_texas" parent="texas">
            <label>King-Texas</label>
            <value>king_hyphen_texas</value>
          </item>
          <item id="cameron" parent="texas">
            <label>Cameron</label>
            <value>cameron</value>
          </item>
          <item id="uganda" parent="africa">
            <label>Uganda</label>
            <value>uganda</value>
          </item>
          <item id="kenya" parent="africa">
            <label>Kenya</label>
            <value>kenya</value>
          </item>
          <item id="netherlands" parent="europe">
            <label>Netherlands</label>
            <value>netherlands</value>
          </item>
        </dynamiclist>
      </instance>
      <instance id="city">
        <dynamiclist>
          <item id="seattle" parent="king">
            <label>Seattle</label>
            <value>seattle</value>
          </item>
          <item id="redmond" parent="king">
            <label>Redmond</label>
            <value>redmond</value>
          </item>
          <item id="tacoma" parent="pierce">
            <label>Tacoma</label>
            <value>tacoma</value>
          </item>
          <item id="puyallup" parent="pierce">
            <label>Puyallup</label>
            <value>puyallup</value>
          </item>
          <item id="dumont" parent="king_hyphen_texas">
            <label>Dumont</label>
            <value>dumont</value>
          </item>
          <item id="finney" parent="king_hyphen_texas">
            <label>Finney</label>
            <value>finney</value>
          </item>
          <item id="brownsville" parent="cameron">
            <label>brownsville</label>
            <value>brownsville</value>
          </item>
          <item id="harlingen" parent="cameron">
            <label>harlingen</label>
            <value>harlingen</value>
          </item>
          <item id="kampala" parent="uganda">
            <label>Kampala</label>
            <value>kampala</value>
          </item>
          <item id="masaka" parent="uganda">
            <label>Masaka</label>
            <value>masaka</value>
          </item>
          <item id="mbale" parent="uganda">
            <label>Mbale</label>
            <value>mbale</value>
          </item>
          <item id="mbarara" parent="uganda">
            <label>Mbarara</label>
            <value>mbarara</value>
          </item>
          <item id="nairobi" parent="kenya">
            <label>Nairobi</label>
            <value>nairobi</value>
          </item>
          <item id="kisumu" parent="kenya">
            <label>Kisumu</label>
            <value>kisumu</value>
          </item>
          <item id="eldoret" parent="kenya">
            <label>Eldoret</label>
            <value>eldoret</value>
          </item>
          <item id="netherlandis" parent="netherlands">
            <label>Netherlandis</label>
            <value>netherlandis</value>
          </item>
          <item id="another_netherlands" parent="netherlands">
            <label>Another Netherlands</label>
            <value>another_netherlands</value>
          </item>
        </dynamiclist>
      </instance>
      <bind id="patient_id" nodeset="/example_study2_example_form2_v1/patient_id" type="string" />
      <bind id="title" nodeset="/example_study2_example_form2_v1/title" type="string" />
      <bind id="first_name" nodeset="/example_study2_example_form2_v1/first_name" type="string" />
      <bind id="last_name" nodeset="/example_study2_example_form2_v1/last_name" type="string" required="true()" />
      <bind id="sex" nodeset="/example_study2_example_form2_v1/sex" type="string" />
      <bind id="birthdate" nodeset="/example_study2_example_form2_v1/birthdate" type="date" constraint=". &lt; today()" jr:constraintMsg="Birthdate Cannot be after today" />
      <bind id="weightkg" nodeset="/example_study2_example_form2_v1/weightkg" type="decimal" constraint=". &gt; 0 and . &lt;= 200" jr:constraintMsg="Should be between 0 and 200 (inclusive)" />
      <bind id="height" nodeset="/example_study2_example_form2_v1/height" type="decimal" constraint=". &gt;= 1 and . &lt;= 9" jr:constraintMsg="Height should be between 1 and 9" />
      <bind id="is_patient_pregnant" nodeset="/example_study2_example_form2_v1/is_patient_pregnant" type="string" relevant="/example_study2_example_form2_v1/sex = 'male'" />
      <bind id="arvs" nodeset="/example_study2_example_form2_v1/arvs" type="string" />
      <bind id="picture" nodeset="/example_study2_example_form2_v1/picture" type="binary" />
      <bind id="sound" nodeset="/example_study2_example_form2_v1/sound" type="binary" />
      <bind id="record_video" nodeset="/example_study2_example_form2_v1/record_video" type="binary" />
      <bind id="region" nodeset="/example_study2_example_form2_v1/region" type="string" />
      <bind id="sub_hyphen_region" nodeset="/example_study2_example_form2_v1/sub_hyphen_region" type="string" />
      <bind id="city" nodeset="/example_study2_example_form2_v1/city" type="string" />
      <bind id="children_number" nodeset="/example_study2_example_form2_v1/children_number" type="string" />
      <bind id="details_of_children" nodeset="/example_study2_example_form2_v1/details_of_children" constraint="count(.) = /example_study2_example_form2_v1/children_number" jr:constraintMsg="Enter details of all children" />
      <bind id="name" nodeset="/example_study2_example_form2_v1/details_of_children/name" type="string" />
      <bind id="age" nodeset="/example_study2_example_form2_v1/details_of_children/age" type="int" />
      <bind id="child_sex" nodeset="/example_study2_example_form2_v1/details_of_children/child_sex" type="string" />
      <bind id="start_time" nodeset="/example_study2_example_form2_v1/start_time" type="time" />
      <bind id="endtime" nodeset="/example_study2_example_form2_v1/endtime" type="time" jr:preload="timestamp" jr:preloadParams="end" />
    </model>
  </h:head>
  <h:body>
    <input ref="/example_study2_example_form2_v1/patient_id">
      <label>Patient ID</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/title">
      <label>Title</label>
      <item>
        <label>Mr</label>
        <value>mr</value>
      </item>
      <item>
        <label>Mrs</label>
        <value>mrs</value>
      </item>
    </select1>
    <input ref="/example_study2_example_form2_v1/first_name">
      <label>First name</label>
    </input>
    <input ref="/example_study2_example_form2_v1/last_name">
      <label>Last name</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/sex">
      <label>Sex</label>
      <item>
        <label>Male</label>
        <value>male</value>
      </item>
      <item>
        <label>Female</label>
        <value>female</value>
      </item>
    </select1>
    <input ref="/example_study2_example_form2_v1/birthdate">
      <label>Birthdate</label>
    </input>
    <input ref="/example_study2_example_form2_v1/weightkg">
      <label>Weight(Kg)</label>
    </input>
    <input ref="/example_study2_example_form2_v1/height">
      <label>Height</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/is_patient_pregnant">
      <label>Is patient pregnant</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <select ref="/example_study2_example_form2_v1/arvs">
      <label>ARVS</label>
      <hint>Please select all anti-retrovirals that the patient is taking</hint>
      <item>
        <label>AZT</label>
        <value>azt</value>
      </item>
      <item>
        <label>ABICVAR</label>
        <value>abicvar</value>
      </item>
      <item>
        <label>EFIVARENCE</label>
        <value>efivarence</value>
      </item>
      <item>
        <label>TRIOMUNE</label>
        <value>triomune</value>
      </item>
      <item>
        <label>TRUVADA</label>
        <value>truvada</value>
      </item>
    </select>
    <upload ref="/example_study2_example_form2_v1/picture" mediatype="image/*">
      <label>Picture</label>
    </upload>
    <upload ref="/example_study2_example_form2_v1/sound" mediatype="audio/*">
      <label>Sound</label>
    </upload>
    <upload ref="/example_study2_example_form2_v1/record_video" mediatype="video/*">
      <label>Record video</label>
    </upload>
    <select1 ref="/example_study2_example_form2_v1/region">
      <label>Region</label>
      <item>
        <label>Washington</label>
        <value>washington</value>
      </item>
      <item>
        <label>Texas</label>
        <value>texas</value>
      </item>
      <item>
        <label>Africa</label>
        <value>africa</value>
      </item>
      <item>
        <label>Europe</label>
        <value>europe</value>
      </item>
    </select1>
    <select1 ref="/example_study2_example_form2_v1/sub_hyphen_region">
      <label>Sub-Region</label>
      <itemset nodeset="instance('sub_hyphen_region')/dynamiclist/item[@parent=/example_study2_example_form2_v1/region]">
        <value ref="value" />
        <label ref="label" />
      </itemset>
    </select1>
    <select1 ref="/example_study2_example_form2_v1/city">
      <label>City</label>
      <itemset nodeset="instance('city')/dynamiclist/item[@parent=/example_study2_example_form2_v1/sub_hyphen_region]">
        <value ref="value" />
        <label ref="label" />
      </itemset>
    </select1>
    <input ref="/example_study2_example_form2_v1/children_number">
      <label>Number of children</label>
    </input>
    <group>
      <label>Details of Children</label>
      <repeat nodeset="/example_study2_example_form2_v1/details_of_children" jr:count="/example_study2_example_form2_v1/children_number">
        <input ref="/example_study2_example_form2_v1/details_of_children/name">
          <label>Name</label>
        </input>
        <input ref="/example_study2_example_form2_v1/details_of_children/age">
          <label>Age</label>
        </input>
        <select1 ref="/example_study2_example_form2_v1/details_of_children/child_sex">
          <label>Sex</label>
          <item>
            <label>Male</label>
            <value>male</value>
          </item>
          <item>
            <label>Female</label>
            <value>female</value>
          </item>
        </select1>
      </repeat>
    </group>
    <input ref="/example_study2_example_form2_v1/start_time">
      <label>Start time</label>
    </input>
    <input ref="/example_study2_example_form2_v1/endtime">
      <label>End time</label>
    </input>
  </h:body>
</h:html>''',
            xmlWithMeta:'''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Example form2</h:title>
    <model>
      <instance>
        <example_study2_example_form2_v1 id="0" name="Example form2">
          <patient_id />
          <title>mr</title>
          <first_name />
          <last_name />
          <sex />
          <birthdate />
          <weightkg />
          <height />
          <is_patient_pregnant />
          <arvs />
          <picture />
          <sound />
          <record_video />
          <region />
          <sub_hyphen_region />
          <city />
          <children_number />
          <details_of_children>
            <name />
            <age />
            <child_sex />
          </details_of_children>
          <start_time />
          <endtime />
          <meta>
            <instanceID />
          </meta>
        </example_study2_example_form2_v1>
      </instance>
      <instance id="sub_hyphen_region">
        <dynamiclist>
          <item id="king" parent="washington">
            <label>King</label>
            <value>king</value>
          </item>
          <item id="pierce" parent="washington">
            <label>Pierce</label>
            <value>pierce</value>
          </item>
          <item id="king_hyphen_texas" parent="texas">
            <label>King-Texas</label>
            <value>king_hyphen_texas</value>
          </item>
          <item id="cameron" parent="texas">
            <label>Cameron</label>
            <value>cameron</value>
          </item>
          <item id="uganda" parent="africa">
            <label>Uganda</label>
            <value>uganda</value>
          </item>
          <item id="kenya" parent="africa">
            <label>Kenya</label>
            <value>kenya</value>
          </item>
          <item id="netherlands" parent="europe">
            <label>Netherlands</label>
            <value>netherlands</value>
          </item>
        </dynamiclist>
      </instance>
      <instance id="city">
        <dynamiclist>
          <item id="seattle" parent="king">
            <label>Seattle</label>
            <value>seattle</value>
          </item>
          <item id="redmond" parent="king">
            <label>Redmond</label>
            <value>redmond</value>
          </item>
          <item id="tacoma" parent="pierce">
            <label>Tacoma</label>
            <value>tacoma</value>
          </item>
          <item id="puyallup" parent="pierce">
            <label>Puyallup</label>
            <value>puyallup</value>
          </item>
          <item id="dumont" parent="king_hyphen_texas">
            <label>Dumont</label>
            <value>dumont</value>
          </item>
          <item id="finney" parent="king_hyphen_texas">
            <label>Finney</label>
            <value>finney</value>
          </item>
          <item id="brownsville" parent="cameron">
            <label>brownsville</label>
            <value>brownsville</value>
          </item>
          <item id="harlingen" parent="cameron">
            <label>harlingen</label>
            <value>harlingen</value>
          </item>
          <item id="kampala" parent="uganda">
            <label>Kampala</label>
            <value>kampala</value>
          </item>
          <item id="masaka" parent="uganda">
            <label>Masaka</label>
            <value>masaka</value>
          </item>
          <item id="mbale" parent="uganda">
            <label>Mbale</label>
            <value>mbale</value>
          </item>
          <item id="mbarara" parent="uganda">
            <label>Mbarara</label>
            <value>mbarara</value>
          </item>
          <item id="nairobi" parent="kenya">
            <label>Nairobi</label>
            <value>nairobi</value>
          </item>
          <item id="kisumu" parent="kenya">
            <label>Kisumu</label>
            <value>kisumu</value>
          </item>
          <item id="eldoret" parent="kenya">
            <label>Eldoret</label>
            <value>eldoret</value>
          </item>
          <item id="netherlandis" parent="netherlands">
            <label>Netherlandis</label>
            <value>netherlandis</value>
          </item>
          <item id="another_netherlands" parent="netherlands">
            <label>Another Netherlands</label>
            <value>another_netherlands</value>
          </item>
        </dynamiclist>
      </instance>
      <bind id="patient_id" nodeset="/example_study2_example_form2_v1/patient_id" type="string" />
      <bind id="title" nodeset="/example_study2_example_form2_v1/title" type="string" />
      <bind id="first_name" nodeset="/example_study2_example_form2_v1/first_name" type="string" />
      <bind id="last_name" nodeset="/example_study2_example_form2_v1/last_name" type="string" required="true()" />
      <bind id="sex" nodeset="/example_study2_example_form2_v1/sex" type="string" />
      <bind id="birthdate" nodeset="/example_study2_example_form2_v1/birthdate" type="date" constraint=". &lt; today()" jr:constraintMsg="Birthdate Cannot be after today" />
      <bind id="weightkg" nodeset="/example_study2_example_form2_v1/weightkg" type="decimal" constraint=". &gt; 0 and . &lt;= 200" jr:constraintMsg="Should be between 0 and 200 (inclusive)" />
      <bind id="height" nodeset="/example_study2_example_form2_v1/height" type="decimal" constraint=". &gt;= 1 and . &lt;= 9" jr:constraintMsg="Height should be between 1 and 9" />
      <bind id="is_patient_pregnant" nodeset="/example_study2_example_form2_v1/is_patient_pregnant" type="string" relevant="/example_study2_example_form2_v1/sex = 'male'" />
      <bind id="arvs" nodeset="/example_study2_example_form2_v1/arvs" type="string" />
      <bind id="picture" nodeset="/example_study2_example_form2_v1/picture" type="binary" />
      <bind id="sound" nodeset="/example_study2_example_form2_v1/sound" type="binary" />
      <bind id="record_video" nodeset="/example_study2_example_form2_v1/record_video" type="binary" />
      <bind id="region" nodeset="/example_study2_example_form2_v1/region" type="string" />
      <bind id="sub_hyphen_region" nodeset="/example_study2_example_form2_v1/sub_hyphen_region" type="string" />
      <bind id="city" nodeset="/example_study2_example_form2_v1/city" type="string" />
      <bind id="children_number" nodeset="/example_study2_example_form2_v1/children_number" type="string" />
      <bind id="details_of_children" nodeset="/example_study2_example_form2_v1/details_of_children" constraint="count(.) = /example_study2_example_form2_v1/children_number" jr:constraintMsg="Enter details of all children" />
      <bind id="name" nodeset="/example_study2_example_form2_v1/details_of_children/name" type="string" />
      <bind id="age" nodeset="/example_study2_example_form2_v1/details_of_children/age" type="int" />
      <bind id="child_sex" nodeset="/example_study2_example_form2_v1/details_of_children/child_sex" type="string" />
      <bind id="start_time" nodeset="/example_study2_example_form2_v1/start_time" type="time" />
      <bind id="endtime" nodeset="/example_study2_example_form2_v1/endtime" type="time" jr:preload="timestamp" jr:preloadParams="end" />
      <bind calculate="concat('uuid:', uuid())" nodeset="/example_study2_example_form2_v1/meta/instanceID" readonly="true()" type="string" />
    </model>
  </h:head>
  <h:body>
    <input ref="/example_study2_example_form2_v1/patient_id">
      <label>Patient ID</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/title">
      <label>Title</label>
      <item>
        <label>Mr</label>
        <value>mr</value>
      </item>
      <item>
        <label>Mrs</label>
        <value>mrs</value>
      </item>
    </select1>
    <input ref="/example_study2_example_form2_v1/first_name">
      <label>First name</label>
    </input>
    <input ref="/example_study2_example_form2_v1/last_name">
      <label>Last name</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/sex">
      <label>Sex</label>
      <item>
        <label>Male</label>
        <value>male</value>
      </item>
      <item>
        <label>Female</label>
        <value>female</value>
      </item>
    </select1>
    <input ref="/example_study2_example_form2_v1/birthdate">
      <label>Birthdate</label>
    </input>
    <input ref="/example_study2_example_form2_v1/weightkg">
      <label>Weight(Kg)</label>
    </input>
    <input ref="/example_study2_example_form2_v1/height">
      <label>Height</label>
    </input>
    <select1 ref="/example_study2_example_form2_v1/is_patient_pregnant">
      <label>Is patient pregnant</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <select ref="/example_study2_example_form2_v1/arvs">
      <label>ARVS</label>
      <hint>Please select all anti-retrovirals that the patient is taking</hint>
      <item>
        <label>AZT</label>
        <value>azt</value>
      </item>
      <item>
        <label>ABICVAR</label>
        <value>abicvar</value>
      </item>
      <item>
        <label>EFIVARENCE</label>
        <value>efivarence</value>
      </item>
      <item>
        <label>TRIOMUNE</label>
        <value>triomune</value>
      </item>
      <item>
        <label>TRUVADA</label>
        <value>truvada</value>
      </item>
    </select>
    <upload ref="/example_study2_example_form2_v1/picture" mediatype="image/*">
      <label>Picture</label>
    </upload>
    <upload ref="/example_study2_example_form2_v1/sound" mediatype="audio/*">
      <label>Sound</label>
    </upload>
    <upload ref="/example_study2_example_form2_v1/record_video" mediatype="video/*">
      <label>Record video</label>
    </upload>
    <select1 ref="/example_study2_example_form2_v1/region">
      <label>Region</label>
      <item>
        <label>Washington</label>
        <value>washington</value>
      </item>
      <item>
        <label>Texas</label>
        <value>texas</value>
      </item>
      <item>
        <label>Africa</label>
        <value>africa</value>
      </item>
      <item>
        <label>Europe</label>
        <value>europe</value>
      </item>
    </select1>
    <select1 ref="/example_study2_example_form2_v1/sub_hyphen_region">
      <label>Sub-Region</label>
      <itemset nodeset="instance('sub_hyphen_region')/dynamiclist/item[@parent=/example_study2_example_form2_v1/region]">
        <value ref="value" />
        <label ref="label" />
      </itemset>
    </select1>
    <select1 ref="/example_study2_example_form2_v1/city">
      <label>City</label>
      <itemset nodeset="instance('city')/dynamiclist/item[@parent=/example_study2_example_form2_v1/sub_hyphen_region]">
        <value ref="value" />
        <label ref="label" />
      </itemset>
    </select1>
    <input ref="/example_study2_example_form2_v1/children_number">
      <label>Number of children</label>
    </input>
    <group>
      <label>Details of Children</label>
      <repeat nodeset="/example_study2_example_form2_v1/details_of_children" jr:count="/example_study2_example_form2_v1/children_number">
        <input ref="/example_study2_example_form2_v1/details_of_children/name">
          <label>Name</label>
        </input>
        <input ref="/example_study2_example_form2_v1/details_of_children/age">
          <label>Age</label>
        </input>
        <select1 ref="/example_study2_example_form2_v1/details_of_children/child_sex">
          <label>Sex</label>
          <item>
            <label>Male</label>
            <value>male</value>
          </item>
          <item>
            <label>Female</label>
            <value>female</value>
          </item>
        </select1>
      </repeat>
    </group>
    <input ref="/example_study2_example_form2_v1/start_time">
      <label>Start time</label>
    </input>
    <input ref="/example_study2_example_form2_v1/endtime">
      <label>End time</label>
    </input>
  </h:body>
</h:html>'''
    ]

    static def formRelativeValidation = [
            form: '''### dd

## dsd

@number
@id n1
Num 1


@number
@validif . < $:n1
@message should be less than n2
Some number
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>dsd</h:title>
    <model>
      <instance>
        <dd_dsd_v1 id="0" name="dsd">
          <n1 />
          <some_number />
        </dd_dsd_v1>
      </instance>
      <bind id="n1" nodeset="/dd_dsd_v1/n1" type="int" />
      <bind id="some_number" nodeset="/dd_dsd_v1/some_number" type="int" constraint=". &lt; /dd_dsd_v1/n1" jr:constraintMsg="should be less than n2" />
    </model>
  </h:head>
  <h:body>
    <input ref="/dd_dsd_v1/n1">
      <label>Num 1</label>
    </input>
    <input ref="/dd_dsd_v1/some_number">
      <label>Some number</label>
    </input>
  </h:body>
</h:html>'''
    ]

    static def formSkipLogicAndActions = [
            form: '''### d

## f

Sex
>m
>f

@showif $sex = 'f\'
[was visible] Are you pregnamt [to @showif $sex = 'f']

@hideif $sex = 'm\'
[was visible] Are you pregnamt [to @hideif $sex = 'm']

@enableif $sex = 'f\'
[was visible] Are you pregnamt [to @enableif $sex = 'f']

@disableif $sex = 'm\'
[was visible] Are you pregnamt [to @disableif $sex = 'm']

@invisible
@showif $sex = 'f\'
[was invisible] Are you pregnamt [to @showif $sex = 'f']

@invisible
@hideif $sex = 'm\'
[was invisible] Are you pregnamt [to @hideif $sex = 'm']

@invisible
@enableif $sex = 'f\'
[was invisible] Are you pregnamt [to @enableif $sex = 'f']

@invisible
@disableif $sex = 'm\'
[was invisible] Are you pregnamt [to @disableif $sex = 'm']

@showif $sex = 'f\'
[was enable] Are you pregnamt [to @showif $sex = 'f']

@hideif $sex = 'm\'
[was enable] Are you pregnamt [to @hideif $sex = 'm']

@enableif $sex = 'f\'
[was enable] Are you pregnamt [to @enableif $sex = 'f']

@disableif $sex = 'm\'
[was enable] Are you pregnamt [to @disableif $sex = 'm']

@readonly
@showif $sex = 'f\'
[was readonly] Are you pregnamt [to @showif $sex = 'f']

@readonly
@hideif $sex = 'm\'
[was readonly] Are you pregnamt [to @hideif $sex = 'm']

@readonly
@enableif $sex = 'f\'
[was readonly] Are you pregnamt [to @enableif $sex = 'f']

@readonly
@disableif $sex = 'm\'
[was readonly] Are you pregnamt [to @disableif $sex = 'm']
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>f</h:title>
    <model>
      <instance>
        <d_f_v1 id="0" name="f">
          <_1sex />
          <_2was_visible_are_you_pregnamt_to_showif_sex_eq_f />
          <_3was_visible_are_you_pregnamt_to_hideif_sex_eq_m />
          <_4was_visible_are_you_pregnamt_to_enableif_sex_eq_f />
          <_5was_visible_are_you_pregnamt_to_disableif_sex_eq_m />
          <_6was_invisible_are_you_pregnamt_to_showif_sex_eq_f />
          <_7was_invisible_are_you_pregnamt_to_hideif_sex_eq_m />
          <_8was_invisible_are_you_pregnamt_to_enableif_sex_eq_f />
          <_9was_invisible_are_you_pregnamt_to_disableif_sex_eq_m />
          <_10was_enable_are_you_pregnamt_to_showif_sex_eq_f />
          <_11was_enable_are_you_pregnamt_to_hideif_sex_eq_m />
          <_12was_enable_are_you_pregnamt_to_enableif_sex_eq_f />
          <_13was_enable_are_you_pregnamt_to_disableif_sex_eq_m />
          <_14was_readonly_are_you_pregnamt_to_showif_sex_eq_f />
          <_15was_readonly_are_you_pregnamt_to_hideif_sex_eq_m />
          <_16was_readonly_are_you_pregnamt_to_enableif_sex_eq_f />
          <_17was_readonly_are_you_pregnamt_to_disableif_sex_eq_m />
        </d_f_v1>
      </instance>
      <bind id="_1sex" nodeset="/d_f_v1/_1sex" type="string" />
      <bind id="_2was_visible_are_you_pregnamt_to_showif_sex_eq_f" nodeset="/d_f_v1/_2was_visible_are_you_pregnamt_to_showif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_3was_visible_are_you_pregnamt_to_hideif_sex_eq_m" nodeset="/d_f_v1/_3was_visible_are_you_pregnamt_to_hideif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_4was_visible_are_you_pregnamt_to_enableif_sex_eq_f" nodeset="/d_f_v1/_4was_visible_are_you_pregnamt_to_enableif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_5was_visible_are_you_pregnamt_to_disableif_sex_eq_m" nodeset="/d_f_v1/_5was_visible_are_you_pregnamt_to_disableif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_6was_invisible_are_you_pregnamt_to_showif_sex_eq_f" nodeset="/d_f_v1/_6was_invisible_are_you_pregnamt_to_showif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_7was_invisible_are_you_pregnamt_to_hideif_sex_eq_m" nodeset="/d_f_v1/_7was_invisible_are_you_pregnamt_to_hideif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_8was_invisible_are_you_pregnamt_to_enableif_sex_eq_f" nodeset="/d_f_v1/_8was_invisible_are_you_pregnamt_to_enableif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_9was_invisible_are_you_pregnamt_to_disableif_sex_eq_m" nodeset="/d_f_v1/_9was_invisible_are_you_pregnamt_to_disableif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_10was_enable_are_you_pregnamt_to_showif_sex_eq_f" nodeset="/d_f_v1/_10was_enable_are_you_pregnamt_to_showif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_11was_enable_are_you_pregnamt_to_hideif_sex_eq_m" nodeset="/d_f_v1/_11was_enable_are_you_pregnamt_to_hideif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_12was_enable_are_you_pregnamt_to_enableif_sex_eq_f" nodeset="/d_f_v1/_12was_enable_are_you_pregnamt_to_enableif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_13was_enable_are_you_pregnamt_to_disableif_sex_eq_m" nodeset="/d_f_v1/_13was_enable_are_you_pregnamt_to_disableif_sex_eq_m" type="string" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_14was_readonly_are_you_pregnamt_to_showif_sex_eq_f" nodeset="/d_f_v1/_14was_readonly_are_you_pregnamt_to_showif_sex_eq_f" type="string" readonly="true()" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_15was_readonly_are_you_pregnamt_to_hideif_sex_eq_m" nodeset="/d_f_v1/_15was_readonly_are_you_pregnamt_to_hideif_sex_eq_m" type="string" readonly="true()" relevant="not(/d_f_v1/_1sex = 'm')" />
      <bind id="_16was_readonly_are_you_pregnamt_to_enableif_sex_eq_f" nodeset="/d_f_v1/_16was_readonly_are_you_pregnamt_to_enableif_sex_eq_f" type="string" relevant="/d_f_v1/_1sex = 'f'" />
      <bind id="_17was_readonly_are_you_pregnamt_to_disableif_sex_eq_m" nodeset="/d_f_v1/_17was_readonly_are_you_pregnamt_to_disableif_sex_eq_m" type="string" readonly="true()" relevant="not(/d_f_v1/_1sex = 'm')" />
    </model>
  </h:head>
  <h:body>
    <select1 ref="/d_f_v1/_1sex">
      <label>1. Sex</label>
      <item>
        <label>m</label>
        <value>m</value>
      </item>
      <item>
        <label>f</label>
        <value>f</value>
      </item>
    </select1>
    <input ref="/d_f_v1/_2was_visible_are_you_pregnamt_to_showif_sex_eq_f">
      <label>2. [was visible] Are you pregnamt [to @showif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_3was_visible_are_you_pregnamt_to_hideif_sex_eq_m">
      <label>3. [was visible] Are you pregnamt [to @hideif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_4was_visible_are_you_pregnamt_to_enableif_sex_eq_f">
      <label>4. [was visible] Are you pregnamt [to @enableif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_5was_visible_are_you_pregnamt_to_disableif_sex_eq_m">
      <label>5. [was visible] Are you pregnamt [to @disableif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_6was_invisible_are_you_pregnamt_to_showif_sex_eq_f">
      <label>6. [was invisible] Are you pregnamt [to @showif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_7was_invisible_are_you_pregnamt_to_hideif_sex_eq_m">
      <label>7. [was invisible] Are you pregnamt [to @hideif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_8was_invisible_are_you_pregnamt_to_enableif_sex_eq_f">
      <label>8. [was invisible] Are you pregnamt [to @enableif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_9was_invisible_are_you_pregnamt_to_disableif_sex_eq_m">
      <label>9. [was invisible] Are you pregnamt [to @disableif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_10was_enable_are_you_pregnamt_to_showif_sex_eq_f">
      <label>10. [was enable] Are you pregnamt [to @showif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_11was_enable_are_you_pregnamt_to_hideif_sex_eq_m">
      <label>11. [was enable] Are you pregnamt [to @hideif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_12was_enable_are_you_pregnamt_to_enableif_sex_eq_f">
      <label>12. [was enable] Are you pregnamt [to @enableif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_13was_enable_are_you_pregnamt_to_disableif_sex_eq_m">
      <label>13. [was enable] Are you pregnamt [to @disableif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_14was_readonly_are_you_pregnamt_to_showif_sex_eq_f">
      <label>14. [was readonly] Are you pregnamt [to @showif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_15was_readonly_are_you_pregnamt_to_hideif_sex_eq_m">
      <label>15. [was readonly] Are you pregnamt [to @hideif $sex = 'm']</label>
    </input>
    <input ref="/d_f_v1/_16was_readonly_are_you_pregnamt_to_enableif_sex_eq_f">
      <label>16. [was readonly] Are you pregnamt [to @enableif $sex = 'f']</label>
    </input>
    <input ref="/d_f_v1/_17was_readonly_are_you_pregnamt_to_disableif_sex_eq_m">
      <label>17. [was readonly] Are you pregnamt [to @disableif $sex = 'm']</label>
    </input>
  </h:body>
</h:html>'''
    ]

    static def multiSelectConversion = [
            form: '''### s
@id f
## f

@boolean
@id c
Condition 1

@id c2
@boolean
Condition 2

@id s
Subjects
>>Physics
>>Calculus
>>Biology

@enableif $s = 'physics'
@id ps
Physics Score

@enableif $s = 'calculus' and ($ps != null or (3-4) = 9) and $s = 'biology'
Other Qn

@id g
Some other quesion

'''
    ]

    static def booleanConversion = [
            form: '''### s

## f

Subjects
>>Physics
>>Calculus
>>Biology

@boolean
@id c
Continue

@enableif $c=true and $c < gone() or /r/g > 9 and /p/e <= 8
Q1

@enableif  $c!=true and $c < gone() or /r/g > 9 and /p/e <= 8
Q2



@enableif   $subjects = 'cal' and (/c/ps != null or (3-4) = 9 or  true!=$c) and $c=true
Q3

@id g
Some other quesion


@id e
Some other quesion

@id ps
Some other quesion

''', xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>f</h:title>
    <model>
      <instance>
        <s_f_v1 id="0" name="f">
          <subjects />
          <c />
          <q1 />
          <q2 />
          <q3 />
          <g />
          <e />
          <ps />
        </s_f_v1>
      </instance>
      <bind id="subjects" nodeset="/s_f_v1/subjects" type="string" />
      <bind id="c" nodeset="/s_f_v1/c" type="string" />
      <bind id="q1" nodeset="/s_f_v1/q1" type="string" relevant="/s_f_v1/c = 'true' and /s_f_v1/c &lt; gone() or /r/g &gt; 9 and /p/e &lt;= 8" />
      <bind id="q2" nodeset="/s_f_v1/q2" type="string" relevant="/s_f_v1/c != 'true' and /s_f_v1/c &lt; gone() or /r/g &gt; 9 and /p/e &lt;= 8" />
      <bind id="q3" nodeset="/s_f_v1/q3" type="string" relevant="selected(/s_f_v1/subjects, 'cal') and (/c/ps != null or (3-4) = 9 or /s_f_v1/c != 'true') and /s_f_v1/c = 'true'" />
      <bind id="g" nodeset="/s_f_v1/g" type="string" />
      <bind id="e" nodeset="/s_f_v1/e" type="string" />
      <bind id="ps" nodeset="/s_f_v1/ps" type="string" />
    </model>
  </h:head>
  <h:body>
    <select ref="/s_f_v1/subjects">
      <label>Subjects</label>
      <item>
        <label>Physics</label>
        <value>physics</value>
      </item>
      <item>
        <label>Calculus</label>
        <value>calculus</value>
      </item>
      <item>
        <label>Biology</label>
        <value>biology</value>
      </item>
    </select>
    <select1 ref="/s_f_v1/c">
      <label>Continue</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <input ref="/s_f_v1/q1">
      <label>Q1</label>
    </input>
    <input ref="/s_f_v1/q2">
      <label>Q2</label>
    </input>
    <input ref="/s_f_v1/q3">
      <label>Q3</label>
    </input>
    <input ref="/s_f_v1/g">
      <label>Some other quesion</label>
    </input>
    <input ref="/s_f_v1/e">
      <label>Some other quesion</label>
    </input>
    <input ref="/s_f_v1/ps">
      <label>Some other quesion</label>
    </input>
  </h:body>
</h:html>'''
    ]

  static def formWithIncompatibleOXDId = [
          form: '''### s

## f

@id pif-s.
Sex


@showif $pif-s. = 'male'  and $. = true
No. Kids
>$dsd-. kdsd
''',

          oxdXML : '''<xforms>
  <model>
    <instance id="s_f_v1">
      <s_f_v1 id="0" name="f" formKey="s_f_v1">
        <pif-s. />
        <no_dot_kids />
      </s_f_v1>
    </instance>
    <bind id="pif-s." nodeset="/s_f_v1/pif-s." type="xsd:string" />
    <bind id="no_dot_kids" nodeset="/s_f_v1/no_dot_kids" type="xsd:string" relevant="/s_f_v1/pif-s. = 'male' and /s_f_v1/no_dot_kids = true" action="show" />
  </model>
  <group id="1">
    <label>Page1</label>
    <input bind="pif-s.">
      <label>Sex</label>
    </input>
    <select1 bind="no_dot_kids">
      <label>No. Kids</label>
      <item id="dsd-.">
        <label>kdsd</label>
        <value>dsd-.</value>
      </item>
    </select1>
  </group>
</xforms>'''
  ]

  static def formWithAppearanceComment = [
          form: '''### Study
                  ## Form

                  @hint app:ex:org.odx.activities.OdxPrefillActivity(table_name='household',key_field='id',display_field='household_head')
                  Question with launch''',

          xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Form</h:title>
    <model>
      <instance>
        <study_form_v1 id="0" name="Form">
          <question_with_launch />
        </study_form_v1>
      </instance>
      <bind id="question_with_launch" nodeset="/study_form_v1/question_with_launch" type="string" />
    </model>
  </h:head>
  <h:body>
    <input ref="/study_form_v1/question_with_launch" appearance="ex:org.odx.activities.OdxPrefillActivity(table_name='household',key_field='id',display_field='household_head')">
      <label>Question with launch</label>
    </input>
  </h:body>
</h:html>'''
  ]


    static def  formWithLayoutAttributes = [
            form: Fixtures.formWithLayoutAndBindAttributes,
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Simple Form</h:title>
    <model>
      <instance>
        <sample_markup_study_simple_form_v1 id="0" name="Simple Form">
          <name />
          <sex />
          <is_pregnant />
          <what_is_your_age />
          <course_units />
          <score_math />
          <score_computer_science />
          <score_history />
          <total_score />
          <children />
          <child_details>
            <child_name />
          </child_details>
          <district />
          <subregion />
        </sample_markup_study_simple_form_v1>
      </instance>
      <instance id="subregion">
        <dynamiclist>
          <item id="king" parent="washington">
            <label>King</label>
            <value>king</value>
          </item>
          <item id="kings" parent="washington">
            <label>Kings</label>
            <value>kings</value>
          </item>
        </dynamiclist>
      </instance>
      <bind id="name" nodeset="/sample_markup_study_simple_form_v1/name" name="1" type="string" />
      <bind id="sex" nodeset="/sample_markup_study_simple_form_v1/sex" name="2" type="string" />
      <bind id="is_pregnant" nodeset="/sample_markup_study_simple_form_v1/is_pregnant" name="3" type="string" />
      <bind id="what_is_your_age" nodeset="/sample_markup_study_simple_form_v1/what_is_your_age" name="4" type="int" />
      <bind id="course_units" nodeset="/sample_markup_study_simple_form_v1/course_units" name="5" type="string" />
      <bind id="score_math" nodeset="/sample_markup_study_simple_form_v1/score_math" name="6" type="int" />
      <bind id="score_computer_science" nodeset="/sample_markup_study_simple_form_v1/score_computer_science" name="7" type="int" />
      <bind id="score_history" nodeset="/sample_markup_study_simple_form_v1/score_history" name="8" type="int" />
      <bind id="total_score" nodeset="/sample_markup_study_simple_form_v1/total_score" name="9" type="string" />
      <bind id="children" nodeset="/sample_markup_study_simple_form_v1/children" name="10" type="int" />
      <bind id="child_details" nodeset="/sample_markup_study_simple_form_v1/child_details" name="11" />
      <bind id="child_name" nodeset="/sample_markup_study_simple_form_v1/child_details/child_name" name="11.1" type="string" />
      <bind id="district" nodeset="/sample_markup_study_simple_form_v1/district" type="string" />
      <bind id="subregion" nodeset="/sample_markup_study_simple_form_v1/subregion" name="13" type="string" />
    </model>
  </h:head>
  <h:body class="pages">
    <input ref="/sample_markup_study_simple_form_v1/name" name="lo 1">
      <label>Name</label>
    </input>
    <select1 ref="/sample_markup_study_simple_form_v1/sex" name="lo 2">
      <label>Sex</label>
      <item>
        <label>male</label>
        <value>male</value>
      </item>
      <item>
        <label>female</label>
        <value>female</value>
      </item>
    </select1>
    <select1 ref="/sample_markup_study_simple_form_v1/is_pregnant" name="lo 3">
      <label>Is pregnant</label>
      <item>
        <label>Yes</label>
        <value>true</value>
      </item>
      <item>
        <label>No</label>
        <value>false</value>
      </item>
    </select1>
    <input ref="/sample_markup_study_simple_form_v1/what_is_your_age" name="lo 4">
      <label>What is your Age</label>
    </input>
    <select ref="/sample_markup_study_simple_form_v1/course_units" name="lo 5">
      <label>Course units</label>
      <hint>Course unit hint</hint>
      <item>
        <label>Computer Science</label>
        <value>computer_science</value>
      </item>
      <item>
        <label>Math</label>
        <value>math</value>
      </item>
      <item>
        <label>History</label>
        <value>history</value>
      </item>
    </select>
    <input ref="/sample_markup_study_simple_form_v1/score_math" name="lo 6">
      <label>Score math</label>
    </input>
    <input ref="/sample_markup_study_simple_form_v1/score_computer_science" name="lo 7">
      <label>Score Computer Science</label>
    </input>
    <input ref="/sample_markup_study_simple_form_v1/score_history" name="lo 8">
      <label>Score History</label>
    </input>
    <input ref="/sample_markup_study_simple_form_v1/total_score" name="lo 9">
      <label>Total score</label>
    </input>
    <input ref="/sample_markup_study_simple_form_v1/children" appearance="w1" name="lo 10">
      <label>Number of children</label>
    </input>
    <group>
      <label>Child details</label>
      <repeat nodeset="/sample_markup_study_simple_form_v1/child_details" jr:count="/sample_markup_study_simple_form_v1/children" name="lo 11">
        <input ref="/sample_markup_study_simple_form_v1/child_details/child_name" name="lo 11.1">
          <label>Child Name</label>
        </input>
      </repeat>
    </group>
    <select1 ref="/sample_markup_study_simple_form_v1/district">
      <label>District</label>
      <hint>Please select you district</hint>
      <item>
        <label>Washington</label>
        <value>washington</value>
      </item>
    </select1>
    <select1 ref="/sample_markup_study_simple_form_v1/subregion" name="lo 13">
      <label>Subregion</label>
      <itemset nodeset="instance('subregion')/dynamiclist/item[@parent=/sample_markup_study_simple_form_v1/district]">
        <value ref="value" />
        <label ref="label" />
      </itemset>
    </select1>
  </h:body>
</h:html>'''
    ]



}
