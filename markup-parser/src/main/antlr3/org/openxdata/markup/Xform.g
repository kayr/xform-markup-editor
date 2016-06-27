grammar Xform;

options{
  output = AST;
}

tokens {
  T_TEXT;
  T_QN;
  T_MULTI_QN;
  T_SINGLE_QN;
  T_DYNAMIC_QN;
  T_REPEAT_QN;
  T_DYNAMIC_OPTIONS;
  T_DYNAMIC_INSTANCE;
  T_STUDY;
  T_FORM;
  T_PAGE;
}

@header {
package org.openxdata.markup;
}



@lexer::header {
package  org.openxdata.markup;
}

@parser::members{

	public void emitErrorMessage(String msg) {
		System.err.println(msg);
		throw new RuntimeException(msg);
	}
	
	public void reportError(RecognitionException e) {
		e.printStackTrace();
		super.reportError(e);
	}

}

@lexer::members{

	public String rl(String s){
		if(s == null) return s;
		return s.replaceAll("\n","").trim();
	}
	
    	public  String unwrap(String text, String wrapper) {
        	int wrapperLength = wrapper.length();
	        return text.substring(wrapperLength, text.length() - wrapperLength);
        
	}
	
	public  String unwrap(String text) {
		return unwrap(text.trim(), "'''").trim();
	}
	
	public void emitErrorMessage(String msg) {
		System.err.println(msg);
		throw new RuntimeException(msg);
	}
}



study 	: 	NEWLINE*
	 	STUDYNAME
	 	form+
	 	SPACE*
	 	-> ^(STUDYNAME form+)
	;

form 	: 	ATTRIBUTE*
	   	FORMNAME
	   	formContent+
	   	-> ^(T_FORM FORMNAME ATTRIBUTE* ^(T_PAGE PAGE["Page1"] formContent+))
	   	|
	   	ATTRIBUTE*
	   	FORMNAME
	   	page+
		-> ^(T_FORM FORMNAME ATTRIBUTE* page+)

	;

page
	:	ATTRIBUTE*
		PAGE
		formContent+
		-> ^(T_PAGE PAGE formContent+)
	;

formContent
	:	question|dynamicInstance
	;

question
	:	repeatQn|txtQn|singleSelQn|multipleSelQn|dynamicOptionsBlock|dynamicQn|csvImport|groupQn
	;

repeatQn
	:
		ATTRIBUTE*
		BEGINREPEATMARKER
		question+
		LEFTBRACE
		-> ^( T_REPEAT_QN ATTRIBUTE* BEGINREPEATMARKER question+)
	;
	
groupQn
	:
		ATTRIBUTE*
		GROUP_MARKER
		question+
		LEFTBRACE
		-> ^( T_PAGE ATTRIBUTE* GROUP_MARKER question+)
	;
	


dynamicInstance
	:	DYNAMICINSTANCEMARKER
		LINECONTENTS+
		LEFTBRACE
		-> ^(T_DYNAMIC_INSTANCE LINECONTENTS+)
	;

dynamicOptionsBlock
	:	 DYNAMICMARKER
		 LINECONTENTS+
		(DYNAMICMARKER|LEFTBRACE)
		->^(T_DYNAMIC_OPTIONS LINECONTENTS+)
	;



csvImport
	:	CSVIMPORT
	;

singleSelQn
	:	txtQn
		(SINGLEOPTION|SINGLEOPTION_MULTI)+
		->^(T_SINGLE_QN txtQn SINGLEOPTION* SINGLEOPTION_MULTI*)
	;


dynamicQn
	:	txtQn
		DYNAMICOPTION
		-> ^(T_DYNAMIC_QN txtQn DYNAMICOPTION)

	;

multipleSelQn
	:	txtQn
		(MULTIPLEOPTION|MULTIPLEOPTION_MULTI)+
		->^(T_MULTI_QN txtQn MULTIPLEOPTION* MULTIPLEOPTION_MULTI*)

	;


txtQn
	:	ATTRIBUTE*
		(LINECONTENTS| MULTILINE_TEXT)
		-> ^(T_QN ATTRIBUTE* LINECONTENTS* MULTILINE_TEXT*)
		
	;
	
	
ATTRIBUTE
	:	SPACE '@' SPACE LINECONTENTS	{setText(rl($LINECONTENTS.text));}
	;
	
	
STUDYNAME
	: 	SPACE '###' LINECONTENTS	{setText(rl($LINECONTENTS.text));}
	;
	
FORMNAME:	SPACE '##' LINECONTENTS		{setText(rl($LINECONTENTS.text));}
	;
	
PAGE	:	SPACE '#>' LINECONTENTS		{setText(rl($LINECONTENTS.text));}
	;
	
MULTILINE_TEXT
	:	SPACE '\'\'\'' ( options {greedy=false;} : . )* '\'\'\'' SPACE NEWLINE {setText(unwrap(getText()));}
	;
	
DYNAMICINSTANCEMARKER
	: 	SPACE 'dynamic_instance' SPACE '{' NEWLINE
		;

DYNAMICMARKER
	: 	SPACE 'dynamic' SPACE '{' NEWLINE
	;
	
GROUP_MARKER
	: 	SPACE 'group' SPACE '{' TEXT_OR_NOT	{setText(rl($TEXT_OR_NOT.text));}	
	;
	
LEFTBRACE
	: 	SPACE '}' SPACE NEWLINE	
	;
BEGINREPEATMARKER
	:	SPACE 'repeat' SPACE '{' LINECONTENTS	{setText(rl($LINECONTENTS.text));}
	;

SINGLEOPTION_MULTI 
	: 	SPACE '>' SPACE MULTILINE_TEXT		{setText(unwrap($MULTILINE_TEXT.text));}
	;
	
MULTIPLEOPTION_MULTI
	:	SPACE '>>' SPACE MULTILINE_TEXT 	{setText(unwrap($MULTILINE_TEXT.text));}
	;
	
MULTIPLEOPTION	
	:	SPACE '>>' LINECONTENTS 		{setText(rl($LINECONTENTS.text));}
	;
	
DYNAMICOPTION	
	:	SPACE '$>' LINECONTENTS 		{setText(rl($LINECONTENTS.text));}
	;

SINGLEOPTION	
	: 	SPACE '>' LINECONTENTS 			{setText(rl($LINECONTENTS.text));}
	;

	
CSVIMPORT
	: 	SPACE 'csv:import' LINECONTENTS     	{setText(rl($LINECONTENTS.text));}	
	;

SPACE	:	('\t'|' ')*
	;

	
EMPTYLINECOMMENT
	:	('\t'|' ')+ (NEWLINE) 			{$channel=HIDDEN;}
	|	SPACE '//' (NEWLINE | LINECONTENTS)	{$channel=HIDDEN;}
	;
	
LINECONTENTS 
	:	LINEXTERS+ NEWLINE  			{setText(rl(getText()));}
	;

NEWLINE :	    ((('\r')? '\n' )+) | EOF
	;
	
fragment LINEXTERS 
	:	~('\r'|'\n')
	;
	
fragment TEXT_OR_NOT
	: 	LINEXTERS* NEWLINE	
	;
	
