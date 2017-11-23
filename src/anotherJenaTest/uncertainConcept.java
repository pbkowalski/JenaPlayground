package anotherJenaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL2;



public class uncertainConcept {
	static String hetinfoprefix = "http://hetinffusion/ontologies/";
    static String beliefOnt = "belief";

	static String beliefOntURI = hetinfoprefix+beliefOnt;
	static String uncertainConcept = beliefOntURI+"#Uncertain_concept";
	static String isEither = beliefOntURI+"#is_either";
	static String dsConcept = beliefOntURI+"#DS_concept";
	static String sourceC = beliefOntURI+"#Source";

	static String dsRelation = beliefOntURI+"#DS_relation";
	static String dsModel = beliefOntURI+"#DS_model";
	static String hasSourceRel = beliefOntURI+"#source";
	
	OntResource uConc;
	
	public uncertainConcept(OntResource uncC){
		List<Boolean> b = new ArrayList<Boolean>();
		uncC.listRDFTypes(true).forEachRemaining(t-> b.add(isUncRes(t)));
		if (b.contains(true)){
			this.uConc = uncC;}
		else{
			this.uConc = null;
		}
	}
	public void reasonAndUpdateModel(){
		OntModel m = this.uConc.getOntModel();
		 String uCOntURI = getOntologyName(uConc.getURI());
	        //remove properties associated with belief ontology or the ontology to which the uncertain concept belongs - 
	        // other ontologies are searched for matches
	        List<Statement> description = uConc.listProperties().toList();
	        description.removeIf(s -> (s.getPredicate().getURI().contains(uCOntURI) 
	        			|| s.getPredicate().getURI().contains(beliefOntURI)
	        			|| s.getObject().asNode().getURI().contains(uCOntURI)
	        			||s.getObject().asNode().getURI().contains(beliefOntURI)
	        			||s.getPredicate().equals(OWL2.topObjectProperty)
	        			));
	        
	        Set<OntResource> targets = new HashSet<OntResource>();
		    for(int i=0; i<description.size(); i++){
		    	Statement s = description.get(i);
	            StmtIterator it = m.listStatements(null, s.getPredicate(), s.getObject());
	            if (i==0){
	            	it.forEachRemaining(t -> targets.add(t.getSubject().as(OntResource.class)));
	            }
	            else{
	                Set<OntResource> foo = new HashSet<OntResource>();
	                it.forEachRemaining(t -> foo.add(t.getSubject().as(OntResource.class)));
	                targets.retainAll(foo);
	            }
		    }
		   // m.write(System.out,"TURTLE");

		    targets.removeIf(t->t.getURI().contains(uCOntURI));
		//    targets.forEach(t -> System.out.println(t.getURI()));
	        OntProperty is_either = m.getOntProperty(isEither);
	        for (OntResource t: targets){
	        	m.add(uConc, is_either, t);
	        }
	        Individual newDSConcept = m.getOntClass(dsConcept).createIndividual("dsconcept"+uConc.getLocalName()+"_1");

	        for (OntResource t: targets){
	        	m.add(t, m.getOntProperty(dsRelation), newDSConcept);
	        }
	        m.add(newDSConcept, m.getOntProperty(dsModel), uConc);
	        m.add(newDSConcept, m.getOntProperty(beliefOntURI + "#mass"),m.createTypedLiteral(1.0));
	        
	        //check if uncertain concept has source
	        if(uConc.hasProperty(m.getOntProperty(hasSourceRel))){
	        	m.add(newDSConcept, m.getProperty(hasSourceRel), uConc.getProperty(m.getProperty(hasSourceRel)).getObject());
	        }
	        //else create new placeholder source
	        else{
	        	m.getOntClass(sourceC).createIndividual("source_"+uConc.getLocalName()+"_1");
	        }
	}
	
	private static Boolean isUncRes (Resource r) {
		if(r.getURI().equals(uncertainConcept)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static String getOntologyName(String URI){
		//assumes that entities in the ontology are separated using # or :  rather than /
		if (URI.contains("#")){
			return URI.split(String.valueOf("#"))[0];
		}
		else if (URI.contains(":")){
			return URI.split(String.valueOf(":"))[0];
		}
		else{
			return URI;}
		}
	}