
package org.openxdata.markup.ui;


import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

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
%state  STRING_SINGLE

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
%}

StartComment = "//"
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

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
  "@calculate"|
  "@barcode"|
  "@gps"|
  "@readonly"|
  "@date"|
  "@datetime"|
  "@time"|
  "}"|
  "@hideif"|
  "@showif"|
  "@disableif" |
  "@enableif"|
  "@skiplogic" |
  "@skipaction" |
  "@validif" |
  "@message" |
  "dynamic{"
                                {
                                yybegin(ATTRIB_TEXT);
                                return token(TokenType.KEYWORD);
                                }

   [:digit:]+                   {  return token(TokenType.KEYWORD2); }

  "@comment"                    {
                                yybegin(Q_COMMENT);
                                return token(TokenType.KEYWORD);
                                }

  
  /* labels */
  "@id"|"###"|"##"              {
                                yybegin(ID_TEXT);
                                return token(TokenType.TYPE3);
                                }

 ">"                            {
                                yybegin(OPTIONS);
                                return token(TokenType.OPERATOR);
                                }


  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }
  . | {LineTerminator}           { return token(TokenType.TYPE2);  }
}



<ATTRIB_TEXT> {
  . *                           { return token(TokenType.DEFAULT); }
  {LineTerminator}              { yybegin(YYINITIAL) ; }
}

<ID_TEXT> {
  . *                           { return token(TokenType.STRING2); }
  {LineTerminator}              { yybegin(YYINITIAL) ; }
}

<OPTIONS>{
  . *                           { return token(TokenType.TYPE); }
 {LineTerminator}               { yybegin(YYINITIAL) ; }
}

<Q_COMMENT>{
  . *                           { return token(TokenType.COMMENT2); }
  {LineTerminator}              { yybegin(YYINITIAL) ; }
}

<<EOF>>                          { return null; }