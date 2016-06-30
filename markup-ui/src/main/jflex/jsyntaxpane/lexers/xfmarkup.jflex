
package org.openxdata.markup.ui;


import jsyntaxpane.Token;
import jsyntaxpane.TokenType;
import java.util.Stack;

%%

%public
%class XFormMarkupLexer
%extends jsyntaxpane.lexers.DefaultJFlexLexer
%final
%unicode
%char
%type Token
%ignorecase
%state ID_TEXT
%state OPTIONS
%state Q_COMMENT
%state ATTRIB_TEXT
%state STRING_DOUBLE
%state STRING_SINGLE
%state FILE_TEXT
%state QN_TEXT
%state XPATH_TEXT
%state STRING_SINGLE
%state STRING_DOUBLE
%state LONG_TEXT
%state LONG_OPTION

%{
    /**
     * Create an empty lexer, yyrset will be called later to reset and assign
     * the reader
     */
    public XFormMarkupLexer() {
        super();
    }

    @Override
    public int yychar() {
        return yychar;
    }

    private Stack<Integer> stack = new Stack<Integer>();

    public void yypushState(int newState) {
      stack.push(yystate());
      yybegin(newState);
    }

    public void yypopState() {
      yybegin(stack.pop());
    }

     public Token yypopState(TokenType tokenType) {
       yypopState();
       return token(tokenType);
    }
%}

StartComment = "//"
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = [ \t\f]+
Identifier = [a-zA-Z_][a-zA-Z0-9_]*
XmlId = {Identifier}(":"{Identifier})?
BindId = "@bind:"{XmlId}
LayoutId = "@layout:"{XmlId}
VariableReference = "$" {Identifier}

Comment = {StartComment} {InputCharacter}* {LineTerminator}?

%%

<YYINITIAL> {
  /*keywords */
   "@"|
  "@number"|
  "@boolean"|
  "@video"|
  "@picture"|
  "@audio"|
  "@decimal"|
  "@required"|
  "@invisible"|
  "repeat{"|
  "group{"|
  "@barcode"|
  "@gps"|
  "@readonly"|
  "@date"|
  "@datetime"|
  "@time"|
  "}"|
  "@skipaction" |
  "@message" |
  "@dbid" |
  "@parent" |
  "@style" |
  "@jrcount" |
  "@appearance" |
  "@default" |
  {LayoutId} |
  {BindId} |
  "dynamic_instance{" |
  "dynamic{"
                                {
                                yypushState(ATTRIB_TEXT);
                                return token(TokenType.KEYWORD);
                                }

  "@hideif"|
  "@showif"|
  "@disableif" |
  "@enableif"|
  "@skiplogic" |
  "@validif" |
  "@calculate"                  {
                                 yypushState(XPATH_TEXT);
                                 return token(TokenType.KEYWORD);
                                }

  "@comment"|"@hint"            {
                                yypushState(Q_COMMENT);
                                return token(TokenType.KEYWORD);
                                }


  /* labels */
  "@id"|"###"|"##"|"#>"|"@absoluteid"
                                {
                                yypushState(ID_TEXT);
                                return token(TokenType.TYPE3);
                                }

 ">"|">>"|"$>"                       {
                                yypushState(OPTIONS);
                                return token(TokenType.TYPE);
                                }

 "csv:import"                    {
                                 yypushState(FILE_TEXT);
                                 return token(TokenType.TYPE3);
                                 }

  \'{3}                         {
                                    yypushState(LONG_TEXT);
                                     tokenStart = yychar;
                                     tokenLength = 3;
                                 }


  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  {WhiteSpace}                  {}
  {LineTerminator}              {}


  .                             {
                                  yypushState(QN_TEXT);
                                  return token(TokenType.TYPE2);
                                 }


}



<ATTRIB_TEXT> {
  . *                           { return token(TokenType.DEFAULT); }
  {LineTerminator}+             { yypopState() ; }
}

<ID_TEXT> {
  . *                           { return token(TokenType.STRING); }
  {LineTerminator}+             { yypopState() ; }
}

<OPTIONS>{
  \'{3}                         {
                                    yypushState(LONG_OPTION);
                                    tokenStart = yychar;
                                    tokenLength = 3;
                                 }
  .                             { return token(TokenType.TYPE); }
 {LineTerminator}+              { yypopState() ; }
}


<XPATH_TEXT> {

"and" | "or" | "mod" | "div" |  "*" |  "/" | "//" | "|" | "+" | "-" | "=" | "!=" | "<" | "<=" | ">" | ">="
                                {return token(TokenType.TYPE);}

   [:digit:] | "false" | "true" {return token(TokenType.NUMBER);}




   {VariableReference}          { return token(TokenType.STRING);}
  \"                            {
                                    yypushState(STRING_DOUBLE);
                                    return token(TokenType.COMMENT);
                                }
  \'                            {yypushState(STRING_SINGLE); return token(TokenType.COMMENT);}
  .                             { return token(TokenType.DEFAULT); }
  {LineTerminator}+             { yypopState() ; }
}

<STRING_SINGLE>{
\'                                {return yypopState(TokenType.COMMENT);}
[^\']                             { return token(TokenType.COMMENT);}
}

<STRING_DOUBLE>{
\"                               { return yypopState(TokenType.COMMENT);}
[^\"]                            { return token(TokenType.COMMENT); }
}

<Q_COMMENT>{
  . *                           { return token(TokenType.COMMENT2); }
  {LineTerminator}+             { yypopState() ; }
}

<FILE_TEXT>{
  . *                           {  return token(TokenType.KEYWORD2); }
  {LineTerminator}+             { yypopState() ; }
}

<QN_TEXT>{
 . *                            {return token(TokenType.TYPE2);}
 {LineTerminator}+              { yypopState() ; }
}

<LONG_TEXT>{
 \'{3}                          {
                                   yypopState();
                                   return token(TokenType.TYPE3, tokenStart, tokenLength + 3);
                                }
 .|{LineTerminator}            { tokenLength += yylength();}
}

<LONG_OPTION>{
 \'{3}                          {
                                   yypopState();
                                   return token(TokenType.TYPE, tokenStart, tokenLength + 3);
                                }
 .|{LineTerminator}            { tokenLength += yylength();}
}

<<EOF>>                         { return null; }