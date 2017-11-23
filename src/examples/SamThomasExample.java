package examples;


import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class SamThomasExample
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    // where the ontology should be
    public static final String SOURCE_URL = "http://www.semanticweb.org/ontologies/2013/3/Ontology1365003423152.owl";

    // where we've stashed it on disk for the time being
    protected static final String SOURCE_FILE = "./src/main/resources/Ontology1365003423152.owl";

    // the namespace of the ontology
    public static final String NS = SOURCE_URL + "#";

    /***********************************/
    /* External signature methods      */
    /***********************************/

    public static void main( String[] args ) {
        new SamThomasExample().run();
    }

    public void run() {
        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );

        loadModel( m );

        // get an OntClass reference to one of the classes in the model
        // note: ideally, we would delegate this step to Jena's schemagen tool
        OntClass attack = m.getOntClass( NS + "Attack" );

        // similarly a reference to the attack duration property,
        // and again, using schemagen would be better
        OntProperty attackDuration = m.getDatatypeProperty( NS + "HasDuration" );

        // create an instance of the attack class to represent the current attack
        Individual attack01 = m.createIndividual( NS + "attack01", attack );

        // add a duration to the attack
        attack01.addProperty( attackDuration, m.createTypedLiteral( 10 ) );

        // finally, print out the model to show that we have some data
        m.write( System.out, "Turtle" );
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /** read the ontology and add it as a sub-model of the given ontmodel */
    protected void loadModel( OntModel m ) {
        FileManager.get().getLocationMapper().addAltEntry( SOURCE_URL, SOURCE_FILE );
        Model baseOntology = FileManager.get().loadModel( SOURCE_URL );
        m.addSubModel( baseOntology );

        // for compactness, add a prefix declaration st: (for Sam Thomas)
        m.setNsPrefix( "st", NS );
    }

}