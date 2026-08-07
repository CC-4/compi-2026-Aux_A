/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		19,
		17
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NOT_ACCEPT,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"4,24:8,12,14,12:2,13,24:18,12,24,23,24:5,5,7,6,24:2,3,24:2,22:10,24:3,1,2,2" +
"4:2,15,16,17,16:8,18,16:6,19,16:7,24:4,20,24,10,21,8,21:8,9,21:6,11,21:7,24" +
":5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,36,
"0,1,2,3,1:2,4,5,1:2,6,1:2,7,1:3,8,1,9,1,10,11,4,12,13,14,15,16,17,18,19,20," +
"21,7,22")[0];

	private int yy_nxt[][] = unpackFromString(23,25,
"1,2,20,26,20,28,29,20,3,34:3,4:2,5,6:2,35,6:2,20,34,7,8,20,-1:27,9,-1:30,34" +
",32,34:2,-1:3,34:3,32,34:4,-1:10,6:4,-1:3,6:8,-1:24,7,-1:3,10:12,-1:2,10:10" +
",-1:8,34:4,-1:3,34:8,-1:2,1,25:3,20,25:9,5,25:8,18,25,1,14:4,24,27,14:6,-1," +
"5,14:10,-1:8,34:3,13,-1:3,34:4,13,34:3,-1:10,6:3,23,-1:3,6:4,23,6:3,-1:8,15" +
",-1:19,25:3,-1,25:9,-1,25:8,-1,25,-1:3,10,-1:28,16,-1:23,11,-1:25,12,-1:25," +
"34:3,21,-1:3,34:4,21,34:3,-1:10,6:3,22,-1:3,6:4,22,6:3,-1:10,34:2,30,34,-1:" +
"3,30,34:7,-1:10,6:2,31,6,-1:3,31,6:7,-1:10,6,33,6:2,-1:3,6:3,33,6:4,-1:2");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -3:
						break;
					case 3:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}
					case -4:
						break;
					case 4:
						{   }
					case -5:
						break;
					case 5:
						{
                                  curr_lineno++;      
                        }
					case -6:
						break;
					case 6:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}
					case -7:
						break;
					case 7:
						{
                                  AbstractSymbol value =  AbstractTable.inttable.addString(yytext());
                                  return new Symbol(TokenConstants.INT_CONST, value);
}
					case -8:
						break;
					case 8:
						{
                                    string_buf.setLength(0);
                                    yybegin(STRING);
}
					case -9:
						break;
					case 9:
						{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }
					case -10:
						break;
					case 10:
						{                   
}
					case -11:
						break;
					case 11:
						{
                                  comment_depth = 1;
                                  yybegin(COMMENT);
}
					case -12:
						break;
					case 12:
						{
                                  return new Symbol (
                                    TokenConstants.ERROR, "Unmatched *)"
                                  );
}
					case -13:
						break;
					case 13:
						{
                                return new Symbol(TokenConstants.CLASS);
                        }
					case -14:
						break;
					case 14:
						{
}
					case -15:
						break;
					case 15:
						{
                                  comment_depth++;
}
					case -16:
						break;
					case 16:
						{
                                  comment_depth--;
                                  if (comment_depth == 0) {
                                    yybegin(YYINITIAL);
                                  }
}
					case -17:
						break;
					case 17:
						{
                                    string_buf.append(yytext());
}
					case -18:
						break;
					case 18:
						{
                                    AbstractSymbol value = 
                                    AbstractTable.stringtable.addString(string_buf.toString());
                                    yybegin(YYINITIAL);
                                    return new Symbol(TokenConstants.STR_CONST, value);
}
					case -19:
						break;
					case 20:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -20:
						break;
					case 21:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}
					case -21:
						break;
					case 22:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}
					case -22:
						break;
					case 23:
						{
                                return new Symbol(TokenConstants.CLASS);
                        }
					case -23:
						break;
					case 24:
						{
}
					case -24:
						break;
					case 25:
						{
                                    string_buf.append(yytext());
}
					case -25:
						break;
					case 26:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -26:
						break;
					case 27:
						{
}
					case -27:
						break;
					case 28:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -28:
						break;
					case 29:
						{ /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
					case -29:
						break;
					case 30:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}
					case -30:
						break;
					case 31:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}
					case -31:
						break;
					case 32:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}
					case -32:
						break;
					case 33:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}
					case -33:
						break;
					case 34:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.OBJECTID, value);
}
					case -34:
						break;
					case 35:
						{
                                  AbstractSymbol value =  AbstractTable.idtable.addString(yytext());
                                  return new Symbol(TokenConstants.TYPEID, value);
}
					case -35:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
