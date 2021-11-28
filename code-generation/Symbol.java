/**
 * Trabalho final da disciplina Construção de Compiladores 2021/2
 * 
 * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5 
 * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7 
 * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
 */
public class Symbol
{
   private String id;
   private int type;
   private int nElem;
   private int tipoBase;


   public Symbol(String id, int type, int nElem, int umTBase) {
      this.id = id;
      this.type = type;
      this.nElem = nElem;
      this.tipoBase = umTBase;
   }

   public Symbol(String id, int type) {
      this(id, type, -1, -1);
   }


   public String getId() {
       return id; 
   }

   public int getType() {
       return type; 
   }
   
   public int countElem() {
       return nElem; 
   }

   public int getBaseType() {
       return tipoBase; 
   }

   
   public String toString() {
       String aux = (nElem != -1) ? "\t array(" + nElem + "): "+tipoBase : "";
       return "Id: " + id + "\t type: " + type + aux;
   }


}
