package failures;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SolicitarPrueba
* @author ontology bean generator
* @version 2019/08/13, 12:51:24
*/
public class SolicitarPrueba implements Predicate {

   /**
* Protege name: falla
   */
   private Falla falla;
   public void setFalla(Falla value) { 
    this.falla=value;
   }
   public Falla getFalla() {
     return this.falla;
   }

}
