
/**
 * Write a description of class Paciente here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Symbol
{
   private String id;
   private int tipo;
   private int nElem;
   private int tipoBase;


   public Symbol(String umId, int umTipo, int ne, int umTBase) {
      id = umId;
      tipo = umTipo;
      nElem = ne;
      tipoBase = umTBase;
   }

   public Symbol(String umId, int umTipo) {
      this(umId, umTipo, -1, -1);
   }


   public String getId() {
       return id; 
   }

   public int getTipo() {
       return tipo; 
   }
   
   public int getNumElem() {
       return nElem; 
   }

   public int getTipoBase() {
       return tipoBase; 
   }

   
   public String toString() {
       String aux = (nElem != -1) ? "\t array(" + nElem + "): "+tipoBase : "";
       return "Id: " + id + "\t tipo: " + tipo + aux;
   }


}
