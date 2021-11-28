//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 3 "CodeGeneration.y"
  /**
   * Trabalho final da disciplina Construção de Compiladores 2021/2
   * 
   * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5
   * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7
   * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
   */
  import java.io.*;
  import java.util.ArrayList;
  import java.util.Stack;
//#line 28 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short ID=257;
public final static short INT=258;
public final static short FLOAT=259;
public final static short BOOL=260;
public final static short NUM=261;
public final static short LIT=262;
public final static short VOID=263;
public final static short TRUE=264;
public final static short FALSE=265;
public final static short MAIN=266;
public final static short READ=267;
public final static short WRITE=268;
public final static short IF=269;
public final static short ELSE=270;
public final static short WHILE=271;
public final static short FOR=272;
public final static short BREAK=273;
public final static short CONTINUE=274;
public final static short EQ=275;
public final static short LEQ=276;
public final static short GEQ=277;
public final static short NEQ=278;
public final static short AND=279;
public final static short OR=280;
public final static short ASAD=281;
public final static short INCR=282;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    4,    0,    6,    8,    5,    3,    3,   11,    9,   10,
   10,   12,   12,   13,    1,    1,    1,    7,    7,   14,
   14,   14,   16,   14,   14,   17,   18,   14,   21,   22,
   23,   14,   24,   14,   14,   14,   26,   25,   25,   19,
   19,   20,   20,   15,   15,   15,   15,   15,   15,   15,
   15,   15,   15,   15,   15,   15,   15,   15,   15,   15,
   15,   15,   15,   15,   15,   15,   15,    2,    2,
};
final static short yylen[] = {                            2,
    0,    3,    0,    0,    9,    2,    0,    0,    4,    1,
    4,    3,    1,    1,    1,    1,    1,    2,    0,    2,
    3,    5,    0,    8,    5,    0,    0,    7,    0,    0,
    0,   12,    0,    7,    2,    2,    0,    3,    0,    1,
    0,    1,    0,    1,    1,    1,    1,    3,    4,    3,
    2,    2,    3,    2,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    1,    4,
};
final static short yydefred[] = {                         1,
    0,    0,   15,   16,   17,    8,    0,    0,    0,    0,
    2,    6,    0,    0,    0,   13,    0,    0,    9,    0,
    0,    0,   14,   12,    3,   11,    0,   19,    0,    0,
   44,   45,   46,    0,    0,    0,   26,    0,    0,    0,
    0,    0,    0,   19,    0,    0,   18,    0,    0,    0,
    0,    0,    0,    0,   35,   36,    0,   52,   54,    0,
    0,    0,   51,    0,    5,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   20,    0,
    0,    0,    0,    0,    0,    0,    0,   53,   21,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   57,   58,   59,    0,    0,    0,    0,    0,    0,
   29,    0,   25,   22,    0,    0,   27,    0,   69,    0,
    0,    0,    0,    0,    0,   37,   34,   28,   30,   24,
    0,    0,   38,    0,   31,    0,   32,
};
final static short yydgoto[] = {                          1,
    6,   45,    7,    2,   11,   27,   29,   46,    8,   14,
    9,   15,   16,   47,   48,  108,   53,  122,   86,  124,
  118,  132,  136,  109,  127,  131,
};
final static short yysindex[] = {                         0,
    0, -229,    0,    0,    0,    0, -251, -229, -239, -241,
    0,    0,  -58,  -25,   -8,    0,   -1, -221,    0, -216,
    1,  -50,    0,    0,    0,    0,  -79,    0,    5,  -45,
    0,    0,    0,    7,    8,    9,    0,   10,   -7,   -6,
 -206,   24,   24,    0,  -52,  -51,    0,   87,   24, -184,
 -180,   24,   43,   24,    0,    0,   -5,    0,    0,   18,
  -33,   24,    0,   24,    0,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,    0,   94,
   47,   48,  158,   24,  158,   32,   24,    0,    0,  158,
  158,  -32,  -32,  -32,  -32,  283,  165,  -32,  -32,  -10,
  -10,    0,    0,    0,    0,   39,   40,   56,   63,  118,
    0,  129,    0,    0,   24,    5,    0,   24,    0,  151,
 -164,    5,  158,   49,   51,    0,    0,    0,    0,    0,
    5,   24,    0,   71,    0,    5,    0,
};
final static short yyrindex[] = {                         0,
    0, -150,    0,    0,    0,    0,    0, -150,    0,    0,
    0,    0,  -43,    0,   55,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   -9,   34,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   58,    0,    0,   25,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   79,   74,    0,  -38,    0,    0,    0,    0,  -39,
  -37,  224,  317,  398,  404,  -35,  -24,  412,  418,  288,
  358,    0,    0,    0,   60,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   66,    0,    0,
  -14,    0,   67,    0,    0,    0,    0,    0,    0,    0,
    0,   92,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   97,  127,    0,    0,    0,   96,    0,    0,    0,
    0,    0,  122, -108,  466,    0,    0,    0,   11,    0,
    0,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=698;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         42,
   14,   50,   40,   48,   78,   67,   43,  121,   64,   76,
   74,   10,   75,  128,   77,   14,   66,   13,   39,   50,
   40,   48,  133,   67,   17,   39,   78,  137,    3,    4,
    5,   76,   18,   19,   66,   20,   77,   42,   21,   22,
   23,   25,   26,   28,   43,   49,   50,   51,   52,   54,
   57,   55,   56,   50,   78,   48,   42,   67,   88,   76,
   74,   68,   75,   43,   77,   68,   68,   68,   66,   68,
   47,   68,   81,   65,   47,   47,   47,   73,   47,   72,
   47,   82,   84,   68,   68,   87,   68,  106,  107,   44,
  111,   89,   47,   47,   68,   47,   49,  113,  114,  115,
   49,   49,   49,  116,   49,  126,   49,  129,   39,  130,
   39,  135,    7,   10,   33,    4,   41,   68,   49,   49,
   69,   49,   23,   78,   43,   42,   47,   44,   76,   74,
   78,   75,   41,   77,   12,   76,   74,   58,   75,   61,
   77,   24,  134,    0,    0,   79,   73,    0,   72,    0,
    0,    0,   49,   73,   78,   72,    0,    0,  117,   76,
   74,    0,   75,    0,   77,   78,    0,    0,    0,    0,
   76,   74,    0,   75,    0,   77,    0,   73,    0,   72,
    0,    0,    0,    0,    0,    0,  105,   78,   73,    0,
   72,  125,   76,   74,   78,   75,    0,   77,    0,   76,
   74,   78,   75,    0,   77,    0,   76,   74,    0,   75,
   73,   77,   72,    0,    0,    0,    0,   73,    0,   72,
    0,  119,    0,   30,   73,    0,   72,   31,   62,   63,
   32,   33,    0,   34,   35,   36,    0,   37,   38,   39,
   40,    0,   39,   67,   67,    0,   39,    0,   41,   39,
   39,    0,   39,   39,   39,   66,   39,   39,   39,   39,
    0,   30,    0,    0,   62,   31,    0,   39,   32,   33,
    0,   34,   35,   36,    0,   37,   38,   39,   40,    0,
   30,    0,   62,   62,   31,   62,   41,   32,   33,    0,
    0,    0,   66,   67,   68,   69,   70,   71,    0,   68,
   68,   68,   68,   68,   68,   41,    0,    0,   47,   47,
   47,   47,   47,   47,   68,   68,   62,    0,    0,   78,
    0,    0,    0,    0,   76,   74,    0,   75,   55,   77,
   55,    0,   55,    0,   49,   49,   49,   49,   49,   49,
   69,   69,   73,    0,   72,    0,   55,   55,    0,   55,
    0,    0,    0,    0,    0,    0,    0,   63,    0,    0,
    0,   66,   67,   68,   69,   70,   71,    0,   66,   67,
   68,   69,   70,   71,    0,   63,   63,    0,   63,    0,
   55,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   66,   67,   68,   69,   70,   71,   56,    0,
   56,    0,   56,   66,   67,   68,   69,   70,   71,   63,
    0,    0,    0,    0,    0,    0,   56,   56,    0,   56,
    0,    0,    0,    0,    0,   66,   67,   68,   69,   70,
   71,    0,   66,   67,   68,   69,   70,   71,   64,   66,
   67,   68,   69,   70,   65,    0,    0,    0,    0,    0,
   56,    0,   60,    0,    0,    0,   64,   64,   61,   64,
    0,    0,   65,   65,    0,   65,    0,    0,    0,    0,
   60,   60,    0,   60,    0,    0,   61,   61,    0,   61,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   64,    0,    0,    0,    0,    0,   65,    0,   62,   62,
   62,   62,   62,   62,   60,    0,    0,   59,   60,    0,
   61,    0,    0,    0,   80,    0,    0,   83,    0,   85,
    0,    0,    0,    0,    0,    0,    0,   90,    0,   91,
    0,   92,   93,   94,   95,   96,   97,   98,   99,  100,
  101,  102,  103,  104,    0,    0,    0,    0,    0,  110,
    0,    0,  112,    0,    0,    0,    0,   66,   67,   68,
   69,    0,   55,   55,   55,   55,   55,   55,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  120,    0,    0,  123,    0,    0,    0,    0,    0,    0,
    0,   63,   63,   63,   63,   63,   63,   85,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   56,   56,   56,   56,   56,   56,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   64,   64,   64,   64,   64,   64,   65,   65,
   65,   65,   65,   65,    0,    0,   60,   60,   60,   60,
   60,   60,   61,   61,   61,   61,   61,   61,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   44,   41,   41,   41,   37,   41,   40,  116,   61,   42,
   43,  263,   45,  122,   47,   59,   41,  257,   33,   59,
   59,   59,  131,   59,  266,   40,   37,  136,  258,  259,
  260,   42,   91,   59,   59,   44,   47,   33,   40,  261,
  257,   41,   93,  123,   40,   91,   40,   40,   40,   40,
  257,   59,   59,   93,   37,   93,   33,   93,   41,   42,
   43,   37,   45,   40,   47,   41,   42,   43,   93,   45,
   37,   47,  257,  125,   41,   42,   43,   60,   45,   62,
   47,  262,   40,   59,   60,   91,   62,   41,   41,  123,
   59,  125,   59,   60,   61,   62,   37,   59,   59,   44,
   41,   42,   43,   41,   45,  270,   47,   59,  123,   59,
  125,   41,  263,   59,   41,  125,   59,   93,   59,   60,
   61,   62,   44,   37,   59,   59,   93,  123,   42,   43,
   37,   45,   41,   47,    8,   42,   43,   41,   45,   44,
   47,   20,  132,   -1,   -1,   59,   60,   -1,   62,   -1,
   -1,   -1,   93,   60,   37,   62,   -1,   -1,   41,   42,
   43,   -1,   45,   -1,   47,   37,   -1,   -1,   -1,   -1,
   42,   43,   -1,   45,   -1,   47,   -1,   60,   -1,   62,
   -1,   -1,   -1,   -1,   -1,   -1,   93,   37,   60,   -1,
   62,   41,   42,   43,   37,   45,   -1,   47,   -1,   42,
   43,   37,   45,   -1,   47,   -1,   42,   43,   -1,   45,
   60,   47,   62,   -1,   -1,   -1,   -1,   60,   -1,   62,
   -1,   93,   -1,  257,   60,   -1,   62,  261,  281,  282,
  264,  265,   -1,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,  257,  279,  280,   -1,  261,   -1,  282,  264,
  265,   -1,  267,  268,  269,  280,  271,  272,  273,  274,
   -1,  257,   -1,   -1,   41,  261,   -1,  282,  264,  265,
   -1,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
  257,   -1,   59,   60,  261,   62,  282,  264,  265,   -1,
   -1,   -1,  275,  276,  277,  278,  279,  280,   -1,  275,
  276,  277,  278,  279,  280,  282,   -1,   -1,  275,  276,
  277,  278,  279,  280,  281,  282,   93,   -1,   -1,   37,
   -1,   -1,   -1,   -1,   42,   43,   -1,   45,   41,   47,
   43,   -1,   45,   -1,  275,  276,  277,  278,  279,  280,
  281,  282,   60,   -1,   62,   -1,   59,   60,   -1,   62,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   41,   -1,   -1,
   -1,  275,  276,  277,  278,  279,  280,   -1,  275,  276,
  277,  278,  279,  280,   -1,   59,   60,   -1,   62,   -1,
   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  275,  276,  277,  278,  279,  280,   41,   -1,
   43,   -1,   45,  275,  276,  277,  278,  279,  280,   93,
   -1,   -1,   -1,   -1,   -1,   -1,   59,   60,   -1,   62,
   -1,   -1,   -1,   -1,   -1,  275,  276,  277,  278,  279,
  280,   -1,  275,  276,  277,  278,  279,  280,   41,  275,
  276,  277,  278,  279,   41,   -1,   -1,   -1,   -1,   -1,
   93,   -1,   41,   -1,   -1,   -1,   59,   60,   41,   62,
   -1,   -1,   59,   60,   -1,   62,   -1,   -1,   -1,   -1,
   59,   60,   -1,   62,   -1,   -1,   59,   60,   -1,   62,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   93,   -1,   -1,   -1,   -1,   -1,   93,   -1,  275,  276,
  277,  278,  279,  280,   93,   -1,   -1,   42,   43,   -1,
   93,   -1,   -1,   -1,   49,   -1,   -1,   52,   -1,   54,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   62,   -1,   64,
   -1,   66,   67,   68,   69,   70,   71,   72,   73,   74,
   75,   76,   77,   78,   -1,   -1,   -1,   -1,   -1,   84,
   -1,   -1,   87,   -1,   -1,   -1,   -1,  275,  276,  277,
  278,   -1,  275,  276,  277,  278,  279,  280,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  115,   -1,   -1,  118,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  275,  276,  277,  278,  279,  280,  132,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  275,  276,  277,  278,  279,  280,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  275,  276,  277,  278,  279,  280,  275,  276,
  277,  278,  279,  280,   -1,   -1,  275,  276,  277,  278,
  279,  280,  275,  276,  277,  278,  279,  280,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=282;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"ID","INT","FLOAT","BOOL","NUM",
"LIT","VOID","TRUE","FALSE","MAIN","READ","WRITE","IF","ELSE","WHILE","FOR",
"BREAK","CONTINUE","EQ","LEQ","GEQ","NEQ","AND","OR","ASAD","INCR",
};
final static String yyrule[] = {
"$accept : prog",
"$$1 :",
"prog : $$1 dList mainF",
"$$2 :",
"$$3 :",
"mainF : VOID MAIN '(' ')' $$2 '{' lcmd $$3 '}'",
"dList : decl dList",
"dList :",
"$$4 :",
"decl : type $$4 declAux ';'",
"declAux : Lid",
"declAux : ID '[' NUM ']'",
"Lid : Lid ',' id",
"Lid : id",
"id : ID",
"type : INT",
"type : FLOAT",
"type : BOOL",
"lcmd : lcmd cmd",
"lcmd :",
"cmd : exp ';'",
"cmd : '{' lcmd '}'",
"cmd : WRITE '(' LIT ')' ';'",
"$$5 :",
"cmd : WRITE '(' LIT $$5 ',' exp ')' ';'",
"cmd : READ '(' ID ')' ';'",
"$$6 :",
"$$7 :",
"cmd : WHILE $$6 '(' exp ')' $$7 cmd",
"$$8 :",
"$$9 :",
"$$10 :",
"cmd : FOR '(' forExp ';' $$8 forStopCriteria ';' $$9 forExp ')' $$10 cmd",
"$$11 :",
"cmd : IF '(' exp $$11 ')' cmd restoIf",
"cmd : BREAK ';'",
"cmd : CONTINUE ';'",
"$$12 :",
"restoIf : ELSE $$12 cmd",
"restoIf :",
"forExp : exp",
"forExp :",
"forStopCriteria : exp",
"forStopCriteria :",
"exp : NUM",
"exp : TRUE",
"exp : FALSE",
"exp : ID",
"exp : assignable '=' exp",
"exp : ID '[' exp ']'",
"exp : assignable ASAD exp",
"exp : assignable INCR",
"exp : INCR assignable",
"exp : '(' exp ')'",
"exp : '!' exp",
"exp : exp '+' exp",
"exp : exp '-' exp",
"exp : exp '*' exp",
"exp : exp '/' exp",
"exp : exp '%' exp",
"exp : exp '>' exp",
"exp : exp '<' exp",
"exp : exp EQ exp",
"exp : exp LEQ exp",
"exp : exp GEQ exp",
"exp : exp NEQ exp",
"exp : exp OR exp",
"exp : exp AND exp",
"assignable : ID",
"assignable : ID '[' exp ']'",
};

//#line 150 "CodeGeneration.y"

private Yylex lexer;

private Tab ts = new Tab();

private int strCount = 0;
private ArrayList<String> strTab = new ArrayList<String>();

private Stack<Integer> pRotCond = new Stack<Integer>();

private Stack<Integer> pRotLoop = new Stack<Integer>();

private int proxRot = 1;

private int currentType = INT;

private int yylex () {
	int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
}


public void yyerror (String error) {
    System.err.println ("Error: " + error + "  line: " + lexer.getLine());
}


public Parser(Reader r) {
    lexer = new Yylex(r, this);
}  

public void setDebug(boolean debug) {
    yydebug = debug;
}

public void listarTS() { ts.show();}

public static void main(String args[]) throws IOException {

    Parser yyparser;
    if ( args.length > 0 ) {
      // parse a file
      yyparser = new Parser(new FileReader(args[0]));
      yyparser.yyparse();
      // yyparser.listarTS();

    }
    else {
      // interactive mode
      System.out.println("\n\tFormato: java Parser entrada.cmm >entrada.s\n");
    }
}

enum IncrType {	Postfix, Prefix }
enum LoopType { For, While }

public void gcIfExp() {
	pRotCond.push(proxRot);  proxRot += 2;
	System.out.println("\tPOPL %EAX");
	System.out.println("\tCMPL $0, %EAX");
	System.out.printf("\tJE rot_%02d\n", pRotCond.peek());
}

public void gcIfEnd() {
	System.out.printf("rot_%02d:\n",pRotCond.peek()+1);
	pRotCond.pop();
}

public void gcElseExp() {
	System.out.printf("\tJMP rot_%02d\n", pRotCond.peek()+1);
	System.out.printf("rot_%02d:\n",pRotCond.peek());
}

public void gcLoopInit(LoopType loopType) {
	switch(loopType) {
		case While:
			pRotLoop.push(proxRot);  proxRot += 2;
			System.out.printf("rot_%02d:\n",pRotLoop.peek());
			break;
		case For:
			pRotLoop.push(proxRot);  proxRot += 4;
			System.out.printf("rot_%02d:\n",pRotLoop.peek()+2);
			break;
	}
}

public void gcLoopStopCriteria(LoopType loopType) {
			/* check stop criteria */
	System.out.println("\tPOPL %EAX   \t# jump if false...");
	System.out.println("\tCMPL $0, %EAX");
	System.out.printf("\tJE rot_%02d  \t# reach stop criteria? \n", (int)pRotLoop.peek()+1);
	switch(loopType) {
		case While:
			break;
		case For:
			System.out.printf("\tJMP rot_%02d \t# going to cmd\n", (int)pRotLoop.peek()+3);
			/* create label for increment*/
			System.out.printf("rot_%02d: \t# for incr (?)\n", pRotLoop.peek());
			break;
	}
}

public void gcForIncrExp() {
	System.out.printf("\tJMP rot_%02d\n", (int)pRotLoop.peek()+2);
	System.out.printf("rot_%02d:\n", (int)pRotLoop.peek()+3);
}

public void gcLoopEnd() {
	System.out.printf("\tJMP rot_%02d \t# finished cmd on previous line\n", pRotLoop.peek());
	System.out.printf("rot_%02d: \t# end of loop\n", (int)pRotLoop.peek()+1);
	pRotLoop.pop();
}

public boolean checkIsArray(String id) {
	Symbol s = ts.find(id);
	if(s != null && s.getType() == Tab.ARRAY) {
		return true;
	}

	return false;
}

public void gcArrayPos() {
	System.out.println("\tPUSHL $4");
	gcExpArit('*');
	System.out.println("\tPOPL %EBX");
}

public void gcAccessArrayPos(String id) {
	gcArrayPos();
	System.out.println("\tPUSHL _" + id + "(%EBX)");
}

public void gcGetVariableValue(String id, boolean isArray) {
	if(isArray) {
		gcAccessArrayPos(id);
		return;
	}
	System.out.println("\tPUSHL _"+id); //fetch ident value -> stack
}

public void gcExpIncr(IncrType incrType, String id) {
	boolean isArray = checkIsArray(id);

	gcGetVariableValue(id, isArray);

	if(incrType == IncrType.Postfix){
			if(isArray) //fetch again (the first will remain in the stack with the old value)
				System.out.println("\tPUSHL _" + id + "(%EBX)");
			else 
				System.out.println("\tPUSHL _"+id); 
	}

	System.out.println("\tPOPL %EAX");
	System.out.println("\tADDL $1, %EAX"); //%EAX contains the result (i hope...)
  //System.out.println("\tPUSHL %EAX");
	//System.out.println("\tPOPL %EDX"); 	
	if(isArray) {//take value										
  	System.out.println("\tMOVL %EAX, _" + id + "(%EBX)"); 	//put to variable (ID)
	}
	else{
  	System.out.println("\tMOVL %EAX, _" + id); 	//put to variable (ID)
	}

	if(incrType == IncrType.Prefix)
		System.out.println("\tPUSHL %EAX"); 	
}

public void gcExpAssign(int opAssign, String id) {
	boolean isArray = checkIsArray(id);
	
	switch (opAssign) {
		case '=': 
			System.out.println("\tPOPL %EDX"); 				//take value
			if(isArray) {
				gcArrayPos();
  			System.out.println("\tMOVL %EDX, _" + id + "(%EBX)"); 	//put to variable (ID)
			}
			else{
  			System.out.println("\tMOVL %EDX, _"+id); 	//put to variable (ID)
			}
	   	System.out.println("\tPUSHL %EDX"); 			//push to the stack again
			break;
		case ASAD: 			//the stak already contains the result of the exp (id += >exp<)
			System.out.println("\tPOPL %ECX"); // gets exp result
			if(isArray) {
				gcArrayPos(); //%EBX contains the position

				System.out.println("\tADDL _" + id + "(%EBX), %ECX"); //%ECX contains the result (i hope...)

  			System.out.println("\tMOVL %ECX, _" + id + "(%EBX)"); 	//put to variable (ID)
			}
			else {
				System.out.println("\tADDL _" + id + ", %ECX");
  			System.out.println("\tMOVL %ECX, _"+id); 	//put to variable (ID)
			}
			break;
	}
}
							
public void gcExpArit(int oparit) {
	System.out.println("\tPOPL %EBX");
	System.out.println("\tPOPL %EAX");

	switch (oparit) {
     	case '+' : System.out.println("\tADDL %EBX, %EAX" ); break;
     	case '-' : System.out.println("\tSUBL %EBX, %EAX" ); break;
     	case '*' : System.out.println("\tIMULL %EBX, %EAX" ); break;
    	case '/': 
        	     System.out.println("\tMOVL $0, %EDX");
        	     System.out.println("\tIDIVL %EBX");
        	     break;
     	case '%': 
        	     System.out.println("\tMOVL $0, %EDX");
        	     System.out.println("\tIDIVL %EBX");
        	     System.out.println("\tMOVL %EDX, %EAX");
        	     break;
    }
   	System.out.println("\tPUSHL %EAX");
}

public void gcExpRel(int oprel) {

    System.out.println("\tPOPL %EAX");
    System.out.println("\tPOPL %EDX");
    System.out.println("\tCMPL %EAX, %EDX");
    System.out.println("\tMOVL $0, %EAX");
    
    switch (oprel) {
       case '<':  		System.out.println("\tSETL  %AL"); break;
       case '>':  		System.out.println("\tSETG  %AL"); break;
       case Parser.EQ:  System.out.println("\tSETE  %AL"); break;
       case Parser.GEQ: System.out.println("\tSETGE %AL"); break;
       case Parser.LEQ: System.out.println("\tSETLE %AL"); break;
       case Parser.NEQ: System.out.println("\tSETNE %AL"); break;
    }
    
	System.out.println("\tPUSHL %EAX");

}

public void gcExpLog(int oplog) {

	System.out.println("\tPOPL %EDX");
 	System.out.println("\tPOPL %EAX");

  System.out.println("\tCMPL $0, %EAX");
 	System.out.println("\tMOVL $0, %EAX");
  System.out.println("\tSETNE %AL");
  System.out.println("\tCMPL $0, %EDX");
  System.out.println("\tMOVL $0, %EDX");
  System.out.println("\tSETNE %DL");

  switch (oplog) {
  		case Parser.OR:  System.out.println("\tORL  %EDX, %EAX");  break;
  		case Parser.AND: System.out.println("\tANDL  %EDX, %EAX"); break;
  }

  System.out.println("\tPUSHL %EAX");
}

public void gcExpNot(){

  System.out.println("\tPOPL %EAX" );
 	System.out.println("\tNEGL %EAX" );
  System.out.println("\tPUSHL %EAX");
}

public void gcWriteLit(String lit, boolean simpleLiteral) {
	strTab.add(lit);
  System.out.println("\tMOVL $_str_"+strCount+"Len, %EDX"); 
	System.out.println("\tMOVL $_str_"+strCount+", %ECX"); 
  System.out.println("\tCALL _writeLit"); 
	if(simpleLiteral){
		System.out.println("\tCALL _writeln"); 
	}
  strCount++;
}

public void gcWriteExp() {
	System.out.println("\tPOPL %EAX"); 
	System.out.println("\tCALL _write");	
	System.out.println("\tCALL _writeln"); 
}

public void gcRead(String id) {
	System.out.println("\tPUSHL $_"+id);
	System.out.println("\tCALL _read");
	System.out.println("\tPOPL %EDX");
	System.out.println("\tMOVL %EAX, (%EDX)");
}

private void gcStart() {
	System.out.println(".text\n\n#\t Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5"); 
	System.out.println("#\t Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7"); 
	System.out.println("#\t Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1 \n#\n"); 
	System.out.println(".GLOBL _start\n\n");  
}

private void gcEnd(){
	
	System.out.println("\n\n");
	System.out.println("#");
	System.out.println("# devolve o controle para o SO (final da main)");
	System.out.println("#");
	System.out.println("\tmov $0, %ebx");
	System.out.println("\tmov $1, %eax");
	System.out.println("\tint $0x80");
	
	System.out.println("\n");
	System.out.println("#");
	System.out.println("# Funcoes da biblioteca (IO)");
	System.out.println("#");
	System.out.println("\n");
	System.out.println("_writeln:");
	System.out.println("\tMOVL $__fim_msg, %ECX");
	System.out.println("\tDECL %ECX");
	System.out.println("\tMOVB $10, (%ECX)");
	System.out.println("\tMOVL $1, %EDX");
	System.out.println("\tJMP _writeLit");
	System.out.println("_write:");
	System.out.println("\tMOVL $__fim_msg, %ECX");
	System.out.println("\tMOVL $0, %EBX");
	System.out.println("\tCMPL $0, %EAX");
	System.out.println("\tJGE _write3");
	System.out.println("\tNEGL %EAX");
	System.out.println("\tMOVL $1, %EBX");
	System.out.println("_write3:");
	System.out.println("\tPUSHL %EBX");
	System.out.println("\tMOVL $10, %EBX");
	System.out.println("_divide:");
	System.out.println("\tMOVL $0, %EDX");
	System.out.println("\tIDIVL %EBX");
	System.out.println("\tDECL %ECX");
	System.out.println("\tADD $48, %DL");
	System.out.println("\tMOVB %DL, (%ECX)");
	System.out.println("\tCMPL $0, %EAX");
	System.out.println("\tJNE _divide");
	System.out.println("\tPOPL %EBX");
	System.out.println("\tCMPL $0, %EBX");
	System.out.println("\tJE _print");
	System.out.println("\tDECL %ECX");
	System.out.println("\tMOVB $'-', (%ECX)");
	System.out.println("_print:");
	System.out.println("\tMOVL $__fim_msg, %EDX");
	System.out.println("\tSUBL %ECX, %EDX");
	System.out.println("_writeLit:");
	System.out.println("\tMOVL $1, %EBX");
	System.out.println("\tMOVL $4, %EAX");
	System.out.println("\tint $0x80");
	System.out.println("\tRET");
	System.out.println("_read:");
	System.out.println("\tMOVL $15, %EDX");
	System.out.println("\tMOVL $__msg, %ECX");
	System.out.println("\tMOVL $0, %EBX");
	System.out.println("\tMOVL $3, %EAX");
	System.out.println("\tint $0x80");
	System.out.println("\tMOVL $0, %EAX");
	System.out.println("\tMOVL $0, %EBX");
	System.out.println("\tMOVL $0, %EDX");
	System.out.println("\tMOVL $__msg, %ECX");
	System.out.println("\tCMPB $'-', (%ECX)");
	System.out.println("\tJNE _reading");
	System.out.println("\tINCL %ECX");
	System.out.println("\tINC %BL");
	System.out.println("_reading:");
	System.out.println("\tMOVB (%ECX), %DL");
	System.out.println("\tCMP $10, %DL");
	System.out.println("\tJE _fimread");
	System.out.println("\tSUB $48, %DL");
	System.out.println("\tIMULL $10, %EAX");
	System.out.println("\tADDL %EDX, %EAX");
	System.out.println("\tINCL %ECX");
	System.out.println("\tJMP _reading");
	System.out.println("_fimread:");
	System.out.println("\tCMPB $1, %BL");
	System.out.println("\tJNE _fimread2");
	System.out.println("\tNEGL %EAX");
	System.out.println("_fimread2:");
	System.out.println("\tRET");
	System.out.println("\n");
}

private void gcDataArea(){
	System.out.println("");		
	System.out.println("#");
	System.out.println("# area de dados");
	System.out.println("#");
	System.out.println(".data");
	System.out.println("#");
	System.out.println("# variaveis globais");
	System.out.println("#");
	ts.genGlobals();	
	System.out.println("");
	
}

private void gcLiteralsArea() { 

  System.out.println("#\n# area de literais\n#");
  System.out.println("__msg:");
	System.out.println("\t.zero 30");
	System.out.println("__fim_msg:");
	System.out.println("\t.byte 0");
	System.out.println("\n");

  for (int i = 0; i<strTab.size(); i++ ) {
    System.out.println("_str_"+i+":");
    System.out.println("\t .ascii \""+strTab.get(i)+"\""); 
	  System.out.println("_str_"+i+"Len = . - _str_"+i);  
	}		
}
//#line 869 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 42 "CodeGeneration.y"
{ gcStart(); }
break;
case 2:
//#line 42 "CodeGeneration.y"
{ gcDataArea(); gcLiteralsArea(); }
break;
case 3:
//#line 45 "CodeGeneration.y"
{ System.out.println("_start:"); }
break;
case 4:
//#line 46 "CodeGeneration.y"
{ gcEnd(); }
break;
case 8:
//#line 53 "CodeGeneration.y"
{currentType = val_peek(0).ival;}
break;
case 11:
//#line 57 "CodeGeneration.y"
{
				 									Symbol node = ts.find(val_peek(3).sval);
    	                		if (node != null) 
                            yyerror("Semantic: variable >" + val_peek(3).sval + "< already defined");
                      		else ts.insert(new Symbol(val_peek(3).sval, Tab.ARRAY, val_peek(1).ival, currentType)); 
			 									}
break;
case 14:
//#line 68 "CodeGeneration.y"
{  
											Symbol node = ts.find(val_peek(0).sval);
    	                if (node != null) 
                        yyerror("Semantic: variable >" + val_peek(0).sval + "< already defined");
                      else ts.insert(new Symbol(val_peek(0).sval, currentType)); 
										}
break;
case 15:
//#line 76 "CodeGeneration.y"
{ yyval.ival = INT; }
break;
case 16:
//#line 77 "CodeGeneration.y"
{ yyval.ival = FLOAT; }
break;
case 17:
//#line 78 "CodeGeneration.y"
{ yyval.ival = BOOL; }
break;
case 20:
//#line 85 "CodeGeneration.y"
{ System.out.println("\tPOPL %EDX \t# remove residue"); /* clear stack residue */ }
break;
case 21:
//#line 86 "CodeGeneration.y"
{ System.out.println("\t\t# block is over..."); }
break;
case 22:
//#line 87 "CodeGeneration.y"
{ gcWriteLit(val_peek(2).sval, true); }
break;
case 23:
//#line 88 "CodeGeneration.y"
{ gcWriteLit(val_peek(0).sval, false); }
break;
case 24:
//#line 89 "CodeGeneration.y"
{ gcWriteExp(); }
break;
case 25:
//#line 91 "CodeGeneration.y"
{ gcRead(val_peek(2).sval); }
break;
case 26:
//#line 92 "CodeGeneration.y"
{ gcLoopInit(LoopType.While); }
break;
case 27:
//#line 93 "CodeGeneration.y"
{ gcLoopStopCriteria(LoopType.While); }
break;
case 28:
//#line 94 "CodeGeneration.y"
{ gcLoopEnd(); }
break;
case 29:
//#line 96 "CodeGeneration.y"
{ gcLoopInit(LoopType.For); }
break;
case 30:
//#line 97 "CodeGeneration.y"
{ gcLoopStopCriteria(LoopType.For); }
break;
case 31:
//#line 98 "CodeGeneration.y"
{ gcForIncrExp(); }
break;
case 32:
//#line 99 "CodeGeneration.y"
{ gcLoopEnd(); }
break;
case 33:
//#line 101 "CodeGeneration.y"
{	gcIfExp(); }
break;
case 34:
//#line 101 "CodeGeneration.y"
{ gcIfEnd(); }
break;
case 35:
//#line 102 "CodeGeneration.y"
{ System.out.printf("\tJMP rot_%02d \t#break\n", pRotLoop.peek()+1); }
break;
case 36:
//#line 103 "CodeGeneration.y"
{ System.out.printf("\tJMP rot_%02d \t#continue\n", pRotLoop.peek()); }
break;
case 37:
//#line 107 "CodeGeneration.y"
{gcElseExp();}
break;
case 39:
//#line 108 "CodeGeneration.y"
{gcElseExp();}
break;
case 40:
//#line 111 "CodeGeneration.y"
{ System.out.println("\tPOPL %EDX"); /* clear stack residue */ }
break;
case 43:
//#line 116 "CodeGeneration.y"
{ System.out.println("\tPUSHL $1"); /* infinite loop*/ }
break;
case 44:
//#line 119 "CodeGeneration.y"
{ System.out.println("\tPUSHL $"+val_peek(0).ival); }
break;
case 45:
//#line 120 "CodeGeneration.y"
{ System.out.println("\tPUSHL $1"); }
break;
case 46:
//#line 121 "CodeGeneration.y"
{ System.out.println("\tPUSHL $0"); }
break;
case 47:
//#line 122 "CodeGeneration.y"
{ System.out.println("\tPUSHL _"+val_peek(0).sval);}
break;
case 48:
//#line 123 "CodeGeneration.y"
{ gcExpAssign('=', val_peek(2).sval); }
break;
case 49:
//#line 124 "CodeGeneration.y"
{ gcAccessArrayPos(val_peek(3).sval);}
break;
case 50:
//#line 125 "CodeGeneration.y"
{ gcExpAssign(ASAD, val_peek(2).sval); }
break;
case 51:
//#line 126 "CodeGeneration.y"
{ gcExpIncr(IncrType.Postfix, val_peek(1).sval); }
break;
case 52:
//#line 127 "CodeGeneration.y"
{ gcExpIncr(IncrType.Prefix, val_peek(0).sval); }
break;
case 54:
//#line 129 "CodeGeneration.y"
{ gcExpNot(); }
break;
case 55:
//#line 130 "CodeGeneration.y"
{ gcExpArit('+'); }
break;
case 56:
//#line 131 "CodeGeneration.y"
{ gcExpArit('-'); }
break;
case 57:
//#line 132 "CodeGeneration.y"
{ gcExpArit('*'); }
break;
case 58:
//#line 133 "CodeGeneration.y"
{ gcExpArit('/'); }
break;
case 59:
//#line 134 "CodeGeneration.y"
{ gcExpArit('%'); }
break;
case 60:
//#line 135 "CodeGeneration.y"
{ gcExpRel('>'); }
break;
case 61:
//#line 136 "CodeGeneration.y"
{ gcExpRel('<'); }
break;
case 62:
//#line 137 "CodeGeneration.y"
{ gcExpRel(EQ); }
break;
case 63:
//#line 138 "CodeGeneration.y"
{ gcExpRel(LEQ); }
break;
case 64:
//#line 139 "CodeGeneration.y"
{ gcExpRel(GEQ); }
break;
case 65:
//#line 140 "CodeGeneration.y"
{ gcExpRel(NEQ); }
break;
case 66:
//#line 141 "CodeGeneration.y"
{ gcExpLog(OR); }
break;
case 67:
//#line 142 "CodeGeneration.y"
{ gcExpLog(AND); }
break;
case 68:
//#line 145 "CodeGeneration.y"
{ yyval.sval = val_peek(0).sval; }
break;
case 69:
//#line 146 "CodeGeneration.y"
{ yyval.sval = val_peek(3).sval; }
break;
//#line 1252 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
