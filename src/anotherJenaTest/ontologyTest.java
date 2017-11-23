package anotherJenaTest;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;


public class ontologyTest {

	public static void main(String[] args){
		

		OntModel omodel = ModelFactory.createOntologyModel();
		omodel.read("anomalies.owl");
	}
	



}
