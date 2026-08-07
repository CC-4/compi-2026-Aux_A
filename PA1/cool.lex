/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int comment_depth = 0;

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case COMMENT:
        comment_depth = 0;
        yybegin(YYINITIAL);
        return new Symbol(
            TokenConstants.ERROR, "EOF in comment"
    );
    
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

%state COMMENT
%state STRING

CLASS = [Cc][Ll][Aa][Ss][Ss]

TYPE_ID = [A-Z][A-Za-z0-9_]*
OBJECT_ID = [a-z][A-Za-z0-9_]*

DIGIT = [0-9]+

COMNT_SIMPLE = "--".*

%%

<YYINITIAL>"=>"			{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }



<YYINITIAL>{COMNT_SIMPLE} {                   
}

<YYINITIAL>"(*" {
                                  comment_depth = 1;
                                  yybegin(COMMENT);
}

<COMMENT>"(*" {
                                  comment_depth++;
                    
}

<YYINITIAL>"*)" {

                                  return new Symbol (
                                    TokenConstants.ERROR, "Unmatched *)"
                                  );
}

<COMMENT>"*)" {

                                  comment_depth--;
                                  if (comment_depth == 0) {
                                    yybegin(YYINITIAL);
                                  }
}

<COMMENT>.   {

}

<YYINITIAL>{CLASS}                 {
                                return new Symbol(TokenConstants.CLASS);
                        }

<YYINITIAL>[ \t\r\f\u000B]    {   }

[\n]         {
                                  curr_lineno++;      
                        }

<YYINITIAL>{TYPE_ID}               {

                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}


<YYINITIAL>{OBJECT_ID}               {

                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}

<YYINITIAL>{DIGIT}                   {

                                  AbstractSymbol value =  AbstractTable.inttable.addString(yytext());
                                  return new Symbol(TokenConstants.INT_CONST, value);

}

<YYINITIAL>[\"]                       {
                                    string_buf.setLength(0);
                                    yybegin(STRING);

}

<STRING>[^\"\n\u0000]*               {

                                    string_buf.append(yytext());

}

<STRING>\\b                            {
                                    string_buf.append('\b');
}

<STRING>\\t                            {
                                    string_buf.append('\t');
}

<STRING>\\f                            {
                                    string_buf.append('\f');
}

<STRING>\\n                            {
                                    string_buf.append('\n');
}


<STRING>[\"]                        {
                                    AbstractSymbol value = 
                                    AbstractTable.stringtable.addString(string_buf.toString());
                                    yybegin(YYINITIAL);
                                    return new Symbol(TokenConstants.STR_CONST, value);
}


.                               { /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
