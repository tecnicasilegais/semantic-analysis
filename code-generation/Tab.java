/**
 * Trabalho final da disciplina Construção de Compiladores 2021/2
 * 
 * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5 
 * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7 
 * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
 * Sarah Lacerda - sarah.silva@edu.pucrs.br - 17104191-6
 */

import java.util.ArrayList;
import java.util.Iterator;

public class Tab {
  private ArrayList<Symbol> symbols;
  public static final int ARRAY = 100;
  public Tab() {
    symbols = new ArrayList<Symbol>();
  }

  public void insert(Symbol node) {
    symbols.add(node);
  }

  public void show() {
    System.out.println("\n\n# Symbol Table:\n");
    for (Symbol node : symbols) {
      System.out.println("# " + node);
    }
  }

  public Symbol find(String id) {
    for (Symbol node : symbols) {
      if (node.getId().equals(id)) {
        return node;
      }
    }
    return null;
  }

  public void genGlobals() {
    // assume que todas variáveis são globais e inteiras / array de int.
    for (Symbol node : symbols) {
      if(node.getType() == ARRAY){
        System.out.println("_" + node.getId() + ":" + " .zero " + (4 * node.countElem()));
        continue;
      }
      System.out.println("_" + node.getId() + ":" + "	.zero 4");
    }
  }

}
