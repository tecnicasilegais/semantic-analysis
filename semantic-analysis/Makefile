# only works with the Java extension of yacc: 
# byacc/j from http://troi.lincom-asg.com/~rjamison/byacc/


#
# atenção, copie para a pasta de tabalho
# os arquivos JFlex.jar e yacc.linux
#
# trocar entre linux e windows pelo comentado do BYACCJ

JFLEX  = java -jar JFlex.jar 
BYACCJ = byaccj -tv -J
#BYACCJ = ./yacc.exe -tv -J
JAVAC  = javac

# targets:

all: Parser.class

run: Parser.class
	java Parser

build: clean Parser.class

clean:
	rm -f *~ *.class Yylex.java Parser.java y.output

Parser.class: Symbol.java Tab.java Yylex.java Parser.java
	$(JAVAC) Parser.java

Yylex.java: lexic.flex
	$(JFLEX) lexic.flex

Parser.java: semantic.y
	$(BYACCJ) semantic.y
