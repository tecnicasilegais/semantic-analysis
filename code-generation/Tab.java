import java.util.ArrayList;
import java.util.Iterator;

public class Tab {
  private ArrayList<Symbol> lista;

  public Tab() {
    lista = new ArrayList<Symbol>();
  }

  public void insert(Symbol nodo) {
    lista.add(nodo);
  }

  public void listar() {
    int cont = 0;
    System.out.println("\n\n# Listagem da tabela de simbolos:\n");
    for (Symbol nodo : lista) {
      System.out.println("# " + nodo);
    }
  }

  public Symbol pesquisa(String umId) {
    for (Symbol nodo : lista) {
      if (nodo.getId().equals(umId)) {
        return nodo;
      }
    }
    return null;
  }

  public void geraGlobais() {
    // assume que todas variáveis são globais e inteiras.
    for (Symbol nodo : lista) {
      System.out.println("_" + nodo.getId() + ":" + "	.zero 4");
    }
  }

}
