package failures;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ActualizarHipotesis
* @author ontology bean generator
* @version 2019/08/13, 12:51:24
*/
public class ActualizarHipotesis implements Predicate {

   /**
* Protege name: hipotesis
   */
   private HipotesisDeFalla hipotesis;
   public void setHipotesis(HipotesisDeFalla value) { 
    this.hipotesis=value;
   }
   public HipotesisDeFalla getHipotesis() {
     return this.hipotesis;
   }

}
