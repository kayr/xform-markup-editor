
# Xform Markup Editor For ODK(Javarosa) and OpenXData XForms

You can generate an [openXdata](http://www.openxdata.org/) or [Open Data Kit(ODK)](https://opendatakit.org/) forms using this editor fast and easy.


**Table of Contents**

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


  - [Download](#download)
  - [Screenshots](#screenshots)
    - [Main window](#main-window)
    - [XML Preview](#xml-preview)
  - [Features:](#features)
  - [Instructions](#instructions)
    - [Adding Appearance To Questions](#adding-appearance-to-questions)
    - [Adding pages(High Level Groups)](#adding-pageshigh-level-groups)
    - [Adding Groups(Inner groups with in pages)](#adding-groupsinner-groups-with-in-pages)
    - [Adding Appearance To Pages/Groups](#adding-appearance-to-pagesgroups)
    - [Single Select and Multiple Select](#single-select-and-multiple-select)
    - [Repeat Questions](#repeat-questions)
    - [Dynamic Single Select or Cascading Select Questions](#dynamic-single-select-or-cascading-select-questions)
      - [Option 1](#option-1)
      - [Option 2](#option-2)
    - [Setting Datatype](#setting-datatype)
    - [Setting Other question attributes(Required/Hidden/Locked)](#setting-other-question-attributesrequiredhiddenlocked)
    - [Assigning an Id to a form](#assigning-an-id-to-a-form)
    - [Assigning ids or binds to questions](#assigning-ids-or-binds-to-questions)
    - [Adding hints/help text to questions](#adding-hintshelp-text-to-questions)
    - [Adding styles to forms](#adding-styles-to-forms)
    - [Adding Arbitrary Bind Attributes and Layout Attributes(e.g appearance)](#adding-arbitrary-bind-attributes-and-layout-attributeseg-appearance)
      - [A compatibility note about OpenXdata generated XForm and Layout/Bind Attributes (OpenXdata Users Only)](#a-compatibility-note-about-openxdata-generated-xform-and-layoutbind-attributes-openxdata-users-only)
    - [Multiline Questions/Options](#multiline-questionsoptions)
    - [Numbering The Questions Automatically](#numbering-the-questions-automatically)
- [Adding Skip/Validation/Calculation Logic](#adding-skipvalidationcalculation-logic)
    - [Adding Skip Logic](#adding-skip-logic)
    - [Adding Validation Logic](#adding-validation-logic)
    - [Adding Calculation Logic](#adding-calculation-logic)
  - [Explaining preferences](#explaining-preferences)
    - [ODK Specific](#odk-specific)
    - [Openxdata specific](#openxdata-specific)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

### Download

A [download](https://github.com/kayr/xform-markup-editor/releases) of the editor is available [here](https://github.com/kayr/xform-markup-editor/releases).
To run this app open the command line and execute `java -jar <file-name>`

### Screenshots

#### Main window
![Editor][main_window]

#### XML Preview
![Editor][show_xml]


### Features:
 - Simple XPath Variable references e.g instead of `/instance/path_id` you use `$path_id`
 - On the fly simple validation of xpath
 - Direct preview in enketo
 - Autocompletion of words using Ctrl-K
 - Easy creation of cascading selects by referencing a CSV. The CSV will data will be baked into the generated XFORM.
 - Auto-numbering of labels and IDS
 - Adding arbitrary layout and bind attributes using `@bind:<bindParameter> value` or `@layout:<layoutParameter> value`
 - If you are designing forms for OpenXData you can select the *Emulate OXD to ODK* preference to convert OXD Xpath to ODK.
   e.g `$multiselect_qn = 'option'` is converted to `selected($multiselect_qn,'option')`
 - Multiline Questions which make it easier to write markdown spanning multiple lines
 - Support for appearance
 - Support for form styles
 - Some simple constructs while writing skip logic (see [Adding Skip Logic](#adding-skip-logic))

### Instructions

You start with entering the study name which starts with ***###***

Followed by the form name which starts with ***##***

Followed by the questions (one question per line).

When you are done simply click the *Show ODK XML* or *Show OXD XML* button to view the XFORM source code.

E.g

```
### Sample Study

## Sample form

First name

Second name

Sex

Age

Is pregnant

Drugs taken

'''This is a question
that spans
multiple lines '''

```


#### Adding Appearance To Questions
You can add appearance to any element(groups,pages and questions) using the ***"@appearance"*** attribute

Look at [this](http://xlsform.org/ref-table/) page for most supported appearances for both enketo and odk

e.g
```
@appearance w1
Question one

@appearance search
Select preferences
 > Option 1
 > Option 2
 > Option 3
 > Option 4
 > Option 5
 > Option 6
```


The above will generate a study named **Sample Study** containing one Form(Sample Form). All questions will be of type Text

#### Adding pages(High Level Groups)
To add a page use the **#> <Page.Name>** marker. The example below generates two pages ***Bio Info Page*** and ***Health Status Page***. Pages normally translate to groups. That is high level groups.

```
### Sample Study

## Sample form

#> Bio Info Page

First name

Second name

Sex

Age

#> Health Status Page

Is pregnant

Drugs taken

```




#### Adding Groups(Inner groups with in pages)

You can wrap questions in a group using the ***group{}*** tag. Only groups that have ids will be labeled with numbers in case you select the ***"number labels"*** or ***"number ids"*** option.
E.g :
```
### study

## form

#> page one(outer group)

Question one

@appearance field-list
group{ This is a group label but its optional
   Question two
   Question three
}
```

You can assign appearance,skiplogic and ids to these groups too
E.g:
```
@appearance field-list
@id nested_group
group{ This is a group label but its optional
   Question two
   Question three
}
```

#### Adding Appearance To Pages/Groups

You can assign appearance,skiplogic and ids to these pages
E.g:
```
@appearance field-list
@id bio_info_page
#> Bio Info Page

@appearance field-list
@id nested_group
group{ This is a group label but its optional
   Question two
   Question three
}
```

#### Single Select and Multiple Select
To add options to questions you simply use `>` or `>>` for single select and multiple select respectively

E.g To add *single select options* to the [Sex] Question

```
Sex
>male
>female
```
and *to add multiple select options* to the [Drugs taken] Question

```
Drugs taken
>>Pain killers
>>Antibiotics
```
'''You can assign variables to the options too'''

e.g ">> $pain_killers Pain Killers" will assign variable '''pain_killers''' to this option




#### Repeat Questions
E.g

```
repeat{ Child details

  Child Name

  Child Sex

  Child Age
}
```
#### Dynamic Single Select or Cascading Select Questions
Dynamic questions are questions whose options change dynamically depending on other questions input.

##### Option 1

To make these you just have to create a CSV file(may be in excel) then paste the content in the markup editor wrapped in the '''dynamic{ }''' tag.  If you wish to make the questions required add a "'''*"''' at the beginning of question header. In the example below region is required. E.g

```
dynamic{
*Region,	Sub-Region,	    City
Washington,	King,	    Seattle
Washington,	King,	    Redmond
Texas,	    King-Texas,	Dumont
Texas,	    King-Texas,	Finney
Texas,	    Cameron,	harlingen
Africa,	    Uganda,	    Kampala
Africa,	    Uganda,	    Masaka
Africa,	    Kenya,	    Kisumu
Africa,	    Kenya,	    Eldoret
Europe,	    Netherlands,Netherlandis
Europe,	    Netherlands,Another Netherlands
}
```

##### Option 2

You can save the CSV as an independent file then import it with the '''csv:import''' syntax

E.g

```
csv:import RelativePathToCsvFile.csv
```
#### Setting Datatype
If you wish to specify data types on the question you just need to add a datatype attribute just before the question.

E.g To make the question [Age] accept numbers only you do as below

```
@number
Age
```
Other datatypes include @number, @decimal, @date, @boolean, @time, @datetime, @picture, @video, @audio, @picture, @gps, @barcode

#### Setting Other question attributes(Required/Hidden/Locked)
To make a question required you add a '''*''' at the beginning of the question

E.g To make question ***First name*** *required*

```
*First name
```
or

```
@required
First name
```
To make it ***hidden***

```
@invisible
First name
```
To make it ***locked or read only***

```
@readonly
First name
```
#### Assigning an Id to a form
To assign an id to a form use the ***@id*** attribute

E.g
```
@id this_is_a_custom_id
## Simple form
```

#### Assigning ids or binds to questions
Sometimes questions have very long bindings or ids and need to be shortened(in order to be exported).Or sometimes you need to assign a question an id if you plan to use it as a reference in skip or validation logic.

To assign an id to a question use the ***@id*** attribute

E.g

```
@id first_name
First name
```
Only lowercase id names are allowed followed by a mix of numbers and underscores like ***first_name_1*** but not ***1_first_name***

#### Adding hints/help text to questions
You can add a hint or help text using the ***@hint*** attribute like this

E.g

```
@hint This is help text for first name question
First name
```
#### Adding styles to forms
You can add styles to forms.
E.g Adding the theme-grid style to a form
```
### study

@style theme-grid
## form name

#> page one
```

#### Adding Arbitrary Bind Attributes and Layout Attributes(e.g appearance)
You can add layout attributes(like appearance,jr:count etc) to questions. See below:

E.g
To make a select-one question with search
```
@appearance search
Select one option
 >option one
 >option two
 >option three
 >option four
```

If you wish to add any other custom layout attribute you can use the ***"@layout:..."*** syntax
E.g The same example below is the same as the one above
```
@layout:appearance search
Select one option
 >option one
 >option two
 >option three
 >option four
```

To preload the username configured in ODK Collect settings page do the following. These properties will be added to the bind section of the xform
```
@bind:jr:preload property
@bind:jr:preloadParams username
@readonly
User name
```

##### A compatibility note about OpenXdata generated XForm and Layout/Bind Attributes (OpenXdata Users Only)
OpenXdata Form Designer does not support layout and bind attributes and therefore these attributes will disappear once you load the form into the openxdata form designer. To work around this, in the editor select the ***Store extra attributes in Comment***  preference then these attributes will be embedded into the hint section of a question. This is important when you are using this editor's library as a bridge between OpenXdata and OpenDataKit. When converting an openXdata xform to OpenDataKit XForm these attributes will be picked up and reinserted into the form.


#### Multiline Questions/Options

To write questions that span multiple lines, simply wrap the question text in triple quotes. This makes it easy to write Markdown

E.g
```
'''This is a question
that spans
multiple lines '''
```

You can also have options that span multiple line

E.g
```
'''
Question text
> '''This is an option
that spans multiple lines'''
>Option 1
```


#### Numbering The Questions Automatically

To number the questions automatically check the ***Number Labels checkbox*** on the toolbar. If you want the numbers to be propagated to the binding then you can also check the ***Preferences -> Number IDs checkbox***.

**N.B** Selecting the ***number ids option*** has one downside in that when a question is added to a form.. all following questions after that question will get a new bindings and hence might make it difficult to analyze data between two form versions.

## Adding Skip/Validation/Calculation Logic
Before the markup editor was for basically generating form content(questions) and groups but it was extended to also support adding of skip/validation/calculation logic. This comes in a little handy if you have to write complex formulas only handled by xforms clients that visual form designers cannot handle. You get a little benefit of basic validation of your formulas and variables. All the formulas are based on plain XPATH syntax. But with simpler question referencing as compared to the raw XML way.

***Referencing question IDs:*** You reference a question in a formula like this ***$\<question-id\>***. To reference the current question variable use the ***"."***

In special circumstances you may want to use ***$.*** to reference the current question, this is because in the generated xform XML the "***$.***" will be replaced by the current question binding which looks like this /forminstance/questionvariable. It is rare for that you will ever need to use "***$.***" usually the dot(.) is enough.

***Note: it is advised you assign ids to all questions that are going to be referenced in your XPATH formulas***

For this section we will refer to the form below. Note that some of the options have assigned ids or variables. Look at the female option for the Sex question, it has an assigned option variable "female_opt"

```
### Sample Study


## Sample form


First name


Second name


@id sex
Sex
>Male
>$female Female

@number
Age


Is pregnant


@id children
Number of children

@id pain_killers
@number
Pain killers taken


@id antibiotics
@number
Antibiotics taken


Total drugs taken

repeat{ Child details

  Child Name

  Child Sex
}
```
#### Adding Skip Logic
  An example that ***shows*** the [Is pregnant] question when selected sex is female

```
@showif $sex = 'female'
Is pregnant
```
An example that ***enables*** the [Is pregnant] question when selected sex is female. In Javarosa clients this behaves the same as @showif

```
@enableif $sex = 'female'
Is pregnant
```
You can even even ***hide***  the question if the person is male.
e.g
```
@hideif $sex = 'male'
Is pregnant
```
For Javarosa clients the above example will be converted to ```relevant=not($sex = 'male')``` after the xform is compiled

Other supported skip logic actions include  ***@hideif, @enableif, @disableif, @showif***

#### Adding Validation Logic
An example that makes sure age is between 10 and 20

```
@validif . >= 10 and . <= 20
@message Age should be between 10 and 20 years
@number
Age
```
The dot(.) means current question The @message attribute sets the message when the validation fails. In this case the message is [Age should be between 10 and 20 years]

Validating a repeat has the correct number of children rows

```
@validif length(.) = $children
repeat{ Child details

  Child Name

  Child Sex
}
```

For Javarosa Client You use jr:count,however the above can be converted to the javarosa(see [Explaining preferences Emulate ODK to OXD ](#explaining-preferences) )
```
@jrcount $children
repeat{ Child details

  Child Name

  Child Sex
}
```
#### Adding Calculation Logic
An example that calculates that total drugs taken i.e [Pain killers taken] + [Antibiotics taken]

```
@calculate $antibiotics + $pain_killers
Total drugs taken
```


### Explaining preferences
  - **Number Labels:** This auto numbers all the question labels and groups(excludes groups without ids)
  - **Number Id:** The propagates the label numbers down to the question bindings
  - **Allow Invalid OXD ids:** The informs the edit to allow all valid XML ids. Without this option selected only lower case ids or bindings are allowed
  - **Ensure unique Identifier Question:** This checks the a form has a unique identifier exists in a form. This question will generate a unique id that will be explicitly unique to any form instance or data. Uncheck this option to turn off the feature

#### ODK Specific
  - **Emulate ODK to OXD:** This makes the editor behave as if its converting oxd forms to ODK. It makes extra effort to ensure that oxd xpath retains the semantics when converted to ODK. especially expressions that contain multi select and single select references.
	  - This mode also makes any date related question with id of `endtime` to become an automatic time stamp i.e it adds the required jr:preload attributes
	  - This add `jr:count` to all repeat questions that have a validation like `length(.) = <value>`
  - **Automatically add meta InstanceID:** This auto add the meta/instanceID element to the form as required by some javarosa clients and servers

#### Openxdata specific
  - **Generate Layout:** Generates layout xml when you export  form
  - **Store extra attributes in comments:** OpenXdata Form Designer does not support layout and bind attributes and therefore these attributes will disappear once you load the form into the openxdata form designer. To work around this, in the editor select the ***Store extra attributes in Comment***  preference then these attributes will be embedded into the hint section of a question.

[main_window]: https://github.com/kayr/xform-markup-editor/blob/master/images/main_window.gif?raw=true
[show_xml]: https://github.com/kayr/xform-markup-editor/blob/master/images/show_xml.PNG?raw=true



