package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.lang.sl.SLCodec;
import failures.*;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.AID;
import jade.content.onto.OntologyException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserInterfaceAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = FailureOntology.getInstance();
	private Vector <AID> agentes = new Vector<AID>();
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		addBehaviour(new AgentFinding());
	}
	private class askUser extends OneShotBehaviour{
		public void action() {
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
			try {
				Thread.sleep(10000);
				System.out.println("------Menú para aviso de fallas-----");
				System.out.println("Que desea hacer?");
				System.out.println("1. Revisar si hay conexión ");
				System.out.println("2. Agregar hipotesis");
				String line=buffer.readLine();
				try {
				switch (line) {
					case "1":
						Falla falla = new Falla();
						falla.setDescripcion("Ping Request");
						SolicitarPrueba testRequest = new SolicitarPrueba();
						testRequest.setFalla(falla);
						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.setLanguage(codec.getName());
						request.setOntology(ontology.getName());
						request.setSender(getAID());
						request.addReceiver(agentes.firstElement());
						getContentManager().fillContent(request, testRequest);
						send(request);
						MessageTemplate mt = MessageTemplate.and(
								MessageTemplate.MatchLanguage(codec.getName()),
								MessageTemplate.MatchOntology(ontology.getName()));
						ACLMessage message = blockingReceive(mt); 
						try {
							if (message != null) {
								if(message.getPerformative()==ACLMessage.INFORM) {
									System.out.println("Recibí el mensaje antes del content");
									ContentElement ce = getContentManager().extractContent(message);
									System.out.println("Y el mensaje es el sigte: ");
								}
							}
						}
						catch(OntologyException on) {
							System.out.println(on);
						}
						catch(jade.content.lang.Codec.CodecException ce) {
							System.out.println(ce);
						}
						break;
					case "2":
						System.out.println("Esto todavía no está funcional");
						break;
					default:
						System.out.println("No escogió ninguna de las opciones dadas");
					
				}
				}
				catch(OntologyException on) {
					System.out.println(on);
				}
				catch(jade.content.lang.Codec.CodecException ce) {
					System.out.println(ce);
				}
			}
			catch (InterruptedException ex) {
	            Logger.getLogger(UserInterfaceAgent.class.getName()).log(Level.SEVERE, null, ex);
	        }
			catch (IOException io) {
				System.out.println(io);
			}
			catch(java.util.NoSuchElementException ne) {
				System.out.println("!!!No se ha llenado el vector con los agentes que prestan el servicio!!!");
			}
			
		}

	}
	
	private class AgentFinding extends CyclicBehaviour{
		public void action() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription serviceToFind = new ServiceDescription();
			serviceToFind.setType("Hypothesis");
			dfd.addServices(serviceToFind);
			try {
				DFAgentDescription [] result = DFService.search(myAgent, dfd);
				agentes.clear();
				for(int i=0;i<result.length;i++) {
					agentes.addElement(result[i].getName());
				}
			}
			catch(FIPAException fe) {
				fe.printStackTrace();
			}
			myAgent.addBehaviour(new askUser());
		}
	}
}
