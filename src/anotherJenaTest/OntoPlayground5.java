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
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.InfModel;
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
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import anotherJenaTest.uncertainConcept;

public class OntoPlayground5 {

  //  static String xmlbase = "http://www.semanticweb.org/pawel/ontologies/2017/9/anomalies";
    static String ontopath1 = "anomalies.owl";
    static String ontopath2 = "roads.owl";
    //static String ontopath3 = "threat_roads.owl";
    static String ontopath3 = "joint.owl";

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
        
        OntModel m =ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
        m.read((new FileInputStream(ontopath3)), null);     
    //    m.write(System.out, "TURTLE");

        OntClass uConcC = m.getOntClass(uncertainConcept);
        

        List<? extends OntResource> uConcs= uConcC.listInstances().toList();
        //just get the first one - for now only one exists anyway
        for (OntResource uConc: uConcs){
            uncertainConcept uC = new uncertainConcept(uConc);
            uC.reasonAndUpdateModel();
        }       
    //    m.write(System.out,"TURTLE");
        
        String rules =
        	    "[r1: (?a http://hetinffusion/ontologies/anomalies#associatedWith ?s), (?s http://hetinffusion/ontologies/anomalies#locatedAt ?X) ->" +
        	    "     [(?t http://hetinffusion/ontologies/roads#linksWith ?a) <-  (?t http://hetinffusion/ontologies/belief#is_either ?X)] ]";
       String testRule =
    		   "[r1: (?a http://hetinffusion/ontologies/anomalies#associatedWith ?s), (?s http://hetinffusion/ontologies/anomalies#locatedAt ?X) -> (?X http://hetinffusion/ontologies/roads#linksWith ?a)]";
        GenericRuleReasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        reasoner.setOWLTranslation(true);
        reasoner.setMode(GenericRuleReasoner.HYBRID);
        reasoner.setTransitiveClosureCaching(true);
        Property temp = ResourceFactory.createProperty("temp:x");
        InfModel inf = ModelFactory.createInfModel(reasoner, m);
        OntResource road = m.getOntResource("http://hetinffusion/ontologies/roads#Road2Bridge");
        OntResource road2 = m.getOntResource("http://hetinffusion/ontologies/anomalies#Road2Bridge");
        Property p = m.getProperty("http://hetinffusion/ontologies/roads#linksWith");
/*
        road.listProperties().forEachRemaining(t -> System.out.println(t.toString()));
        road2.listProperties().forEachRemaining(t -> System.out.println(t.toString()));*/
        
        if (road.isSameAs(road2)){
        	System.out.println("true");
        }
        StmtIterator i = inf.listStatements(null, p, (RDFNode)null);
        while (i.hasNext()) {
            System.out.println(" - " + PrintUtil.print(i.nextStatement()));
        }
     /*   StmtIterator i2 = inf.listStatements(null, r, (RDFNode)null);
        while (i2.hasNext()) {
            System.out.println(" - " + PrintUtil.print(i2.nextStatement()));
        }*/
    }  
}


