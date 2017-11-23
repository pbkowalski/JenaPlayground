package anotherJenaTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
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
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

import anotherJenaTest.HIFResource;
import anotherJenaTest.HIFResourceImp;

public class OntoPlayground2 {

  //  static String xmlbase = "http://www.semanticweb.org/pawel/ontologies/2017/9/anomalies";
    static String ontopath1 = "anomalies.owl";
    static String ontopath2 = "roads.owl";
    static String ontopath3 = "threat_roads.owl";
    static String threatClass = "http://hetinfofusion/ontologies/threat/threat";
    static String targetClass = "http://hetinfofusion/ontologies/threat/Target";
    static String hasTargetProperty = "http://hetinfofusion/ontologies/threat/target";
    public static void main(String[] args) throws FileNotFoundException {

        manageOntologies();
    }

    public static void manageOntologies() throws FileNotFoundException{
     //   OntModel anomaliesModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    //    anomaliesModel.read((new FileInputStream(ontopath1)), null);        
        OntModel throadsModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
        throadsModel.read((new FileInputStream(ontopath3)), null);     
//        throadsModel.write(System.out);
        
        //find all threats
        StmtIterator iter = throadsModel.listStatements(null, RDF.type, ResourceFactory.createProperty("http://hetinfofusion/ontologies/threat/Threat"));
        List<Resource> threatList= new ArrayList<Resource>();

        while (iter.hasNext()) {
        	threatList.add(iter.nextStatement().getSubject());
        }
        Resource target = null;
        for(Resource s: threatList){
        	System.out.println(s.toString());
            iter = throadsModel.listStatements(s, ResourceFactory.createProperty("http://hetinfofusion/ontologies/threat/target"), (RDFNode)null);
            while (iter.hasNext()) {
            //	System.out.println(iter.nextStatement().toString());
            	
            	target = iter.nextStatement().getResource();
            	System.out.println(target.toString());
            	iter = throadsModel.listStatements(target, null, (RDFNode)null);
            	while(iter.hasNext()){
            		System.out.println(iter.nextStatement().toString());
            		String test = target.getURI();
            		System.out.println(test);
            		//HIFResourceWrapper x = new HIFResourceWrapper(target);
            	//	x.getMostSpecificClasses();
            	}
            }
        }
        
        //TODO: infer lowest level class(es) of resource
        //take resource, genereate list of classes, identify classes which do not have any subclasses on that list
        

    }   
}