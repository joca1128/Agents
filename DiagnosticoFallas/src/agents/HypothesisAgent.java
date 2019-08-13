package agents;

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



public class HypothesisAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = FailureOntology.getInstance();
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
							System.out.println("Recibí el mensaje y la descripción es:");
							SolicitarPrueba req=(SolicitarPrueba)ce; 
							System.out.println(req.getFalla().getDescripcion());
						}
					}
				}
				else {
					block();
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
