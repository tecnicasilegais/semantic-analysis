  /**
   * Trabalho final da disciplina Construção de Compiladores 2021/2
   * 
   * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5
   * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7
   * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
   */

%%

%byaccj

%{
  private Parser yyparser;

  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
    yyline = 1;
  }


  public int getLine() {
      return yyline;
  }

%}

NUM = [0-9]+
D_NUM = [0-9]+\.[0-9]+
NL  = \n|\r|\r\n

%%


"$TRACE_ON"  { yyparser.setDebug(true);  }
"$TRACE_OFF" { yyparser.setDebug(false); }
"$MOSTRA_TS" { yyparser.showTable(); }

{D_NUM} { yyparser.yylval = new ParserVal(Double.parseDouble(yytext())); 
         return Parser.D_NUM; }

{NUM}  { yyparser.yylval = new ParserVal(Integer.parseInt(yytext())); 
         return Parser.NUM; }

"++"   { return Parser.INCR; }
"--"   { return Parser.DECR; }
">="   { return Parser.GTEQ; }
"<="   { return Parser.LTEQ; }
"=="   { return Parser.EQ; }
"!="   { return Parser.NEQ; }

/* operators */
"+" |
"-" | 
"*" |
"=" |
">" |
"<" |
";" |
"(" |
")" |
"," |
"." |
"!" |
"\{" |
"\}" |
"\[" | 
"\]"    { return (int) yycharat(0); }

"&&"  	{ return Parser.AND; }
"||"  	{ return Parser.OR; }

int     { return Parser.INT; }
double  { return Parser.DOUBLE; }
bool    { return Parser.BOOL; }
string  { return Parser.STRING; }
void    { return Parser.VOID; }
if      { return Parser.IF; }
else    { return Parser.ELSE; }
struct  { return Parser.STRUCT; }
funct   { return Parser.FUNCT; }
return  { return Parser.RETURN; }
false   { return Parser.FALSE; }
true    { return Parser.TRUE; }

[a-zA-Z][a-zA-Z_0-9]* { yyparser.yylval = new ParserVal(yytext());
                     return Parser.IDENT; }

\"[^\"]*\" { yyparser.yylval = new ParserVal(yytext());
             return Parser.LITERAL; }



{NL}   {yyline++;}
[ \t]+ { }

.    { System.err.println("Error: unexpected character '"+yytext()+"' at line "+yyline); return YYEOF; }
