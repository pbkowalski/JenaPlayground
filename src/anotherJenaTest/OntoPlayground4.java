package anotherJenaTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

import anotherJenaTest.HIFResource;
import anotherJenaTest.HIFResourceImp;

public class OntoPlayground4 {

  //  static String xmlbase = "http://www.semanticweb.org/pawel/ontologies/2017/9/anomalies";
    static String ontopath1 = "anomalies.owl";
    static String ontopath2 = "roads.owl";
    static String ontopath3 = "threat_roads.owl";
    static String hetinfoprefix = "http://hetinffusion/ontologies/";
    static String roadsOnt = "roads";
    static String threatOnt = "threat";
    static String beliefOnt = "belief";
    static String beliefOntURI = hetinfoprefix+beliefOnt;
    static String threatClass = hetinfoprefix+threatOnt+"#Threat";
    static String uncertainConcept = beliefOntURI+"#Uncertain_concept";
    static String isEither = beliefOntURI+"#is_either";
    static String dsConcept = beliefOntURI+"#DS_concept";
    static String sourceC = beliefOntURI+"#Source";

    static String dsRelation = beliefOntURI+"#DS_relation";
    static String dsModel = beliefOntURI+"#DS_model";
    static String hasSourceRel = beliefOntURI+"#source";


    static String targetClass = hetinfoprefix+threatOnt+"#Target";
    static String hasTargetProperty = hetinfoprefix+threatOnt+"#target";
    static String outputFile = "output.owl";
    public static void main(String[] args) throws FileNotFoundException {

        manageOntologies();
    }

    public static void manageOntologies() throws FileNotFoundException{
     //   OntModel anomaliesModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    //    anomaliesModel.read((new FileInputStream(ontopath1)), null);        
        OntModel m =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
        m.read((new FileInputStream(ontopath3)), null);     
    //    m.write(System.out, "TURTLE");


        OntClass uConcC = m.getOntClass(uncertainConcept);


        List<? extends OntResource> uConcs= uConcC.listInstances().toList();
        //just get the first one - for now only one exists anyway
        OntResource uConc = uConcs.get(0);
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
	    description.forEach(t -> System.out.println(t.toString()));

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
      //  m.write(System.out,"TURTLE");

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

