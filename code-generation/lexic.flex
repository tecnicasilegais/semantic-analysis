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
NL  = \n | \r | \r\n

%%


"$TRACE_ON"  { yyparser.setDebug(true);  }
"$TRACE_OFF" { yyparser.setDebug(false); }
"$MOSTRA_TS" { yyparser.listarTS(); }


{NL}   {yyline++;}
[ \t]+ { }

/* operadores */

"+" |
"-" |
"*" | 
"/" | 
"%" | 
">" |
"<" |
"=" |
"!" |
";" |
"(" | 
")" |
"{" |
"}" |
"," |
"\[" | 
"\]"    { return (int) yycharat(0); }

{NUM}  { yyparser.yylval = new ParserVal(Integer.parseInt(yytext())); 
         return Parser.NUM; }

"+="   { return Parser.ASAD; }
"++"   { return Parser.INCR; }

"=="   { return Parser.EQ; }
"<="   { return Parser.LEQ; }
">="   { return Parser.GEQ; }
"!="   { return Parser.NEQ; }

"&&"   { return Parser.AND; }
"||"   { return Parser.OR; }

int      { return Parser.INT;}
float    { return Parser.FLOAT; }
bool     { return Parser.BOOL; }
void     { return Parser.VOID; }
main     { return Parser.MAIN; }
write    { return Parser.WRITE; }
read     { return Parser.READ; }
while    { return Parser.WHILE; }
if       { return Parser.IF; }
else     { return Parser.ELSE; }
true     { return Parser.TRUE; }
false    { return Parser.FALSE; }
break    { return Parser.BREAK; }
continue { return Parser.CONTINUE; }
for      { return Parser.FOR; }



[a-zA-Z]+([a-zA-Z0-9]+)? { yyparser.yylval = new ParserVal(yytext());
            return Parser.ID; }

\"[^\n]+\" { yyparser.yylval = new ParserVal(yytext().substring(1, yylength() -1));
	     return Parser.LIT; }

[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
