package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.lang.sl.SLCodec;
import failures.*;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

import ejecuciones.Ping;

public class ExecutionAgent extends Agent{
	private String response;
	private Codec codec = new SLCodec();
	private Ontology ontology = FailureOntology.getInstance();
	private AID sender;
	public void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription service = new ServiceDescription();
		service.setType("executioner");
		service.setName(getLocalName()+"-executioner");
		dfd.addServices(service);
		try {
			DFService.register(this, dfd);
		}
		catch(FIPAException fp){
			System.out.println(fp);
		}
		addBehaviour(new ejecutar());
	}
	
	private class ejecutar extends CyclicBehaviour{
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
											MessageTemplate.MatchLanguage(codec.getName()),
											MessageTemplate.MatchOntology(ontology.getName()));
			ACLMessage message = blockingReceive(mt); 
			try {
				if (message != null) {
					if(message.getPerformative()==ACLMessage.REQUEST) {
						ContentElement ce = getContentManager().extractContent(message);
							if (ce instanceof SolicitarEjecucion) {
								sender=message.getSender();
								SolicitarEjecucion req=(SolicitarEjecucion)ce; 
								switch (req.getCodigoAEjecutar()) {
								case "1":
									System.out.println("Ejecutando ping.");
									response=Ping.runSystemCommand("ping "+"google.com");
									System.out.println("Creando mensaje.");
									ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
									reply.addReceiver(sender);
									reply.setOntology(ontology.getName());
									reply.setLanguage(codec.getName());
									NotificarEjecucion not=new NotificarEjecucion();
									not.setEstado(response);
									getContentManager().fillContent(reply, not);
									send(reply);
									System.out.println("Enviandolo..."+response);
									System.out.println(sender);
									sender=null;
									break;
								default:
									System.out.println("No conozco este código a ejecutar");
								}							
								
							}
					}
				}
			}
			catch(OntologyException on) {
				System.out.println(on);
			}
			catch(jade.content.lang.Codec.CodecException ce) {
				System.out.println(ce);
			}
			
		}
	}
	protected void TakeDown() {
		try {
			DFService.deregister(this);
		}
		catch(FIPAException fp){
			System.out.println(fp);
		}
	}
}
