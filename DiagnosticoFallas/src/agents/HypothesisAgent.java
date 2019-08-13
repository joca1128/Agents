package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.lang.sl.SLCodec;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import failures.*;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;



public class HypothesisAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = FailureOntology.getInstance();
	private Vector <AID> ejecutores = new Vector<AID>();
	private AID sender;
	public void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription service = new ServiceDescription();
		service.setType("Hypothesis");
		service.setName(getLocalName()+"-hypothesis-performing");
		dfd.addServices(service);
		try {
			DFService.register(this, dfd);
		}
		catch(FIPAException fp){
			System.out.println(fp);
		}
		addBehaviour(new prueba());
	}
	private class prueba extends CyclicBehaviour{
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
											MessageTemplate.MatchLanguage(codec.getName()),
											MessageTemplate.MatchOntology(ontology.getName()));
			ACLMessage message = blockingReceive(mt); 
			try {
				if (message != null) {
					if(message.getPerformative()==ACLMessage.REQUEST) {
						ContentElement ce = getContentManager().extractContent(message);
							if (ce instanceof SolicitarPrueba) {
								sender=message.getSender();
								SolicitarPrueba req=(SolicitarPrueba)ce; 
								System.out.println(req.getFalla().getDescripcion());
								DFAgentDescription dfd = new DFAgentDescription();
								ServiceDescription serviceToFind = new ServiceDescription();
								serviceToFind.setType("executioner");
								dfd.addServices(serviceToFind);
								System.out.println("----------------------------------------");
								System.out.println("----------------------------------------");
								try {
									DFAgentDescription [] result = DFService.search(myAgent, dfd);
									ejecutores.clear();
									for(int i=0;i<result.length;i++) {
										ejecutores.addElement(result[i].getName());
									}
								}
								catch(FIPAException fe) {
									fe.printStackTrace();
								}
								SolicitarEjecucion es = new SolicitarEjecucion();
								es.setCodigoAEjecutar("1");
								ACLMessage ExMessage = new ACLMessage(ACLMessage.REQUEST);
								ExMessage.addReceiver(ejecutores.firstElement());
								ExMessage.setLanguage(codec.getName());
								ExMessage.setOntology(ontology.getName());
								getContentManager().fillContent(ExMessage,es);
								send(ExMessage);
								System.out.println(sender);
							}
					}
					else if(message.getPerformative()==ACLMessage.INFORM){
						ContentElement ce = getContentManager().extractContent(message);
						if (ce instanceof NotificarEjecucion) {
							NotificarEjecucion not=(NotificarEjecucion)ce;
							ACLMessage ExMessage = new ACLMessage(ACLMessage.INFORM);
							ExMessage.addReceiver(sender);
							ExMessage.setLanguage(codec.getName());
							ExMessage.setOntology(ontology.getName());
							getContentManager().fillContent(ExMessage,not);
							send(ExMessage);
							sender=null;
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
			System.out.println("Me morí");
			DFService.deregister(this);
		}
		catch(FIPAException fp){
			System.out.println(fp);
		}
	}

	
}
