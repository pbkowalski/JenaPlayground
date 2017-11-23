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

public class OntoPlayground3 {

  //  static String xmlbase = "http://www.semanticweb.org/pawel/ontologies/2017/9/anomalies";
    static String ontopath1 = "anomalies.owl";
    static String ontopath2 = "roads.owl";
    static String ontopath3 = "threat_roads.owl";
    static String hetinfoprefix = "http://hetinffusion/ontologies/";
    static String roadsOnt = "roads";
    static String threatOnt = "threat";
    static String beliefOnt = "belief";

    static String threatClass = hetinfoprefix+threatOnt+"/Threat";
    static String uncertainConcept = hetinfoprefix+beliefOnt+"/Uncertain_concept";

    static String targetClass = hetinfoprefix+threatOnt+"/Target";
    static String hasTargetProperty = hetinfoprefix+threatOnt+"/target";
    public static void main(String[] args) throws FileNotFoundException {

        manageOntologies();
    }

    public static void manageOntologies() throws FileNotFoundException{
     //   OntModel anomaliesModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    //    anomaliesModel.read((new FileInputStream(ontopath1)), null);        
        OntModel m =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
        m.read((new FileInputStream(ontopath3)), null);     
//        throadsModel.write(System.out);
        
        //find all threats
    //    ExtendedIterator<OntClass> iter = throadsModel.listNamedClasses();
    //    while(iter.hasNext()){
    //    	System.out.println(iter.next().getURI());
     //   }
        OntClass threatC = m.getOntClass(threatClass);
        OntProperty hasTargetP = m.getObjectProperty(hasTargetProperty);
        ExtendedIterator<? extends OntResource> iter = threatC.listInstances();
        OntResource R = null;
            while(iter.hasNext()){
            	R = iter.next();
            	if (R.getPropertyValue(hasTargetP) instanceof OntResource){
            		R = (OntResource) R.getPropertyValue(hasTargetP);}
            	else{System.out.println("bug");}
           }
        //System.out.println(R.getRDFType().getURI());
        
        ExtendedIterator<Resource>iter2 = R.listRDFTypes(true);
   /*     while(iter2.hasNext()){
        	System.out.println(iter2.next().toString());
        }*/
        StmtIterator iter3 = R.listProperties();
        List<Statement> propertiesList = iter3.toList();
        List<Statement> roadsPropList = new ArrayList<Statement>();
        for (Statement s: propertiesList){
        	//debug
        	if(s.getPredicate().getURI().contains(hetinfoprefix+roadsOnt)){
        		roadsPropList.add(s);
        	}
        	else if(s.getObject().isURIResource()){
        		if(s.getResource().getURI().contains(hetinfoprefix+roadsOnt)){
        			roadsPropList.add(s);
        		}
        	}
        	
        }
        //ugly, temporary way of doing that
        Set<Individual> targets = m.listIndividuals().toSet();
        targets.removeIf(t -> !(t.getURI().contains(hetinfoprefix+roadsOnt)));
        for (Statement s: roadsPropList){
            StmtIterator it = m.listStatements(null, s.getPredicate(), s.getObject());
            Set<Statement> rset = it.toSet();
            Set<Individual> foo = new HashSet<Individual>();
            rset.forEach( (st) -> foo.add(st.getSubject().as(Individual.class)));
            targets.retainAll(foo);
        }
        for(Individual target: targets){
        	System.out.println(target.getURI());
        }
        
        /*if( iter3.nextStatement().getPredicate().as(OntProperty.class).isObjectProperty()){
        		//System.out.println("true");
        	}*/
//        	System.out.println(iter3.nextStatement().toString());
 //       List<OntResource> threatList = new ArrayList<OntResource>(); 

        
     
        
        //TODO: infer lowest level class(es) of resource
        //take resource, genereate list of classes, identify classes which do not have any subclasses on that list
        

    }   
}