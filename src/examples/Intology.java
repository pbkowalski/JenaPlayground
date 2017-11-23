package examples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

public class Intology {

  //  static String xmlbase = "http://www.semanticweb.org/pawel/ontologies/2017/9/anomalies";
    static String ontopath1 = "anomalies.owl";
    static String ontopath2 = "roads.owl";

    public static void main(String[] args) throws FileNotFoundException {

        manageOntologies();
    }

    public static void manageOntologies() throws FileNotFoundException{
        OntModel anomaliesModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
        anomaliesModel.read((new FileInputStream(ontopath1)), null);        
        OntModel roadsModel =ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
        roadsModel.read((new FileInputStream(ontopath2)), null);     

        //find the intersection of the two ontologies
        Model sharedModel = roadsModel.intersection(anomaliesModel);
        //find all individuals
        Selector selector = new SimpleSelector(null, RDF.type, OWL2.NamedIndividual);
        StmtIterator iter = sharedModel.listStatements(selector);
        List<Resource> sharedResourceList= new ArrayList<Resource>();

        if (iter.hasNext()) {
            System.out.println("shared model contains:");
            while (iter.hasNext()) {
            	sharedResourceList.add(iter.nextStatement().getSubject());
               // System.out.println("  " + iter.nextStatement()
               //                               .getString());
                for(Resource s: sharedResourceList){
                	System.out.println(s.toString());
                }
            }
        } else {
            System.out.println("nothing found");
} 
        Model jointModel = roadsModel.union(anomaliesModel);
        OntModel jointOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, jointModel);
		jointOntModel.write(System.out, "TURTLE");


    }   
}