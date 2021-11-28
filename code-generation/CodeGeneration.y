	
%{
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
%}
 

%token ID, INT, FLOAT, BOOL, NUM, LIT, VOID, TRUE, FALSE
%token MAIN, READ, WRITE, IF, ELSE
%token WHILE, FOR, BREAK, CONTINUE
%token EQ, LEQ, GEQ, NEQ 
%token AND, OR
%token ASAD, INCR

// https://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B#Operator_precedence

%right '=' ASAD				// 16
%left OR						// 15
%left AND						// 14
%left  '>' '<' EQ LEQ GEQ NEQ	// 9 ~ 10
%left '+' '-'					// 6
%left '*' '/' '%'				// 5
%left '!' INCR '['				// 3  (prefix incr ~ postfix incr: 2)

%type <sval> ID
%type <sval> LIT
%type <ival> NUM
%type <ival> type
%type <sval> assignable


%%

prog: { gcStart(); } dList mainF { gcDataArea(); gcLiteralsArea(); } 
	;

mainF: VOID MAIN '(' ')'   { System.out.println("_start:"); }
    	'{' lcmd  { gcEnd(); } '}'
     ; 

dList: decl dList 
	 | 
	 ;

decl: type {currentType = $1;} declAux ';' 
    ;

declAux: Lid
			 | ID '[' NUM ']' {
				 									Symbol node = ts.find($1);
    	                		if (node != null) 
                            yyerror("Semantic: variable >" + $1 + "< already defined");
                      		else ts.insert(new Symbol($1, Tab.ARRAY, $3, currentType)); 
			 									}
			 ;

Lid: Lid ',' id
	 | id
	 ;
id: ID							{  
											Symbol node = ts.find($1);
    	                if (node != null) 
                        yyerror("Semantic: variable >" + $1 + "< already defined");
                      else ts.insert(new Symbol($1, currentType)); 
										}
	;

type: INT    { $$ = INT; }
    | FLOAT  { $$ = FLOAT; }
    | BOOL   { $$ = BOOL; }
    ;

lcmd: lcmd cmd
	|
	;
	   
cmd:  exp ';'		 						{ System.out.println("\tPOPL %EDX \t# remove residue"); /* clear stack residue */ }
	 | '{' lcmd '}' 					{ System.out.println("\t\t# block is over..."); }
	 | WRITE '(' LIT ')' ';' 	{ gcWriteLit($3, true); }
   | WRITE '(' LIT 					{ gcWriteLit($3, false); }
		',' exp ')' ';'					{ gcWriteExp(); }

   | READ '(' ID ')' ';'		{ gcRead($3); }
   | WHILE 	{ gcLoopInit(LoopType.While); } 
			'(' exp ')' { gcLoopStopCriteria(LoopType.While); } 
			cmd		{ gcLoopEnd(); }  

	 | FOR '(' forExp ';' { gcLoopInit(LoopType.For); } 
	 		forStopCriteria ';' { gcLoopStopCriteria(LoopType.For); }  
			forExp ')' { gcForIncrExp(); } 
			cmd { gcLoopEnd(); } 

	 | IF '(' exp 	{	gcIfExp(); } ')' cmd restoIf { gcIfEnd(); }
	 | BREAK 	 	';' { System.out.printf("\tJMP rot_%02d \t#break\n", pRotLoop.peek()+1); }
	 | CONTINUE ';' { System.out.printf("\tJMP rot_%02d \t#continue\n", pRotLoop.peek()); }
   ;
     
     
restoIf: ELSE  {gcElseExp();} cmd  
			 | 			 {gcElseExp();} 
			 ;										

forExp: exp { System.out.println("\tPOPL %EDX"); /* clear stack residue */ }
			|
			;

forStopCriteria: exp
							 | 			{ System.out.println("\tPUSHL $1"); /* infinite loop*/ }
			 				 ;

exp: NUM  									{ System.out.println("\tPUSHL $"+$1); } 
   | TRUE  									{ System.out.println("\tPUSHL $1"); } 
   | FALSE  								{ System.out.println("\tPUSHL $0"); }   
	 | ID											{ System.out.println("\tPUSHL _"+$1);}
   | assignable '=' exp			{ gcExpAssign('=', $1); }
	 | ID '[' exp ']' 				{ gcAccessArrayPos($1);}   
	 | assignable ASAD exp 		{ gcExpAssign(ASAD, $1); }
	 | assignable INCR				{ gcExpIncr(IncrType.Postfix, $1); }
	 | INCR assignable				{ gcExpIncr(IncrType.Prefix, $2); }		
   | '(' exp ')'
   | '!' exp      					{ gcExpNot(); }
   | exp '+' exp						{ gcExpArit('+'); }
   | exp '-' exp						{ gcExpArit('-'); }
   | exp '*' exp						{ gcExpArit('*'); }
   | exp '/' exp						{ gcExpArit('/'); }
   | exp '%' exp						{ gcExpArit('%'); }
   | exp '>' exp						{ gcExpRel('>'); }
   | exp '<' exp						{ gcExpRel('<'); }  									
   | exp EQ exp							{ gcExpRel(EQ); }											
   | exp LEQ exp						{ gcExpRel(LEQ); }											
   | exp GEQ exp						{ gcExpRel(GEQ); }											
   | exp NEQ exp						{ gcExpRel(NEQ); }											
   | exp OR exp							{ gcExpLog(OR); }											
   | exp AND exp						{ gcExpLog(AND); }
   ;		
	
assignable: ID   						{ $$ = $1; }	
					| ID '[' exp ']' 	{ $$ = $1; }	
					;

%%

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