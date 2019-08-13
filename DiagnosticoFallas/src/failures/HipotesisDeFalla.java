package failures;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HipotesisDeFalla
* @author ontology bean generator
* @version 2019/08/13, 12:51:24
*/
public class HipotesisDeFalla implements Concept {

   /**
* Protege name: codigoAEjecutar
   */
   private String codigoAEjecutar;
   public void setCodigoAEjecutar(String value) { 
    this.codigoAEjecutar=value;
   }
   public String getCodigoAEjecutar() {
     return this.codigoAEjecutar;
   }

   /**
* Protege name: id_unico
   */
   private int id_unico;
   public void setId_unico(int value) { 
    this.id_unico=value;
   }
   public int getId_unico() {
     return this.id_unico;
   }

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
