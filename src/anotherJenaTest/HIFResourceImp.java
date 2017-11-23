package anotherJenaTest;

import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.enhanced.EnhNode;
import org.apache.jena.enhanced.Implementation;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.impl.OntResourceImpl;

public class HIFResourceImp extends OntResourceImpl implements HIFResource{

    @SuppressWarnings("hiding")
    public static Implementation factory = new Implementation() {
        @Override
        public EnhNode wrap( Node n, EnhGraph eg ) {
            if (canWrap( n, eg )) {
                return new HIFResourceImp( n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to OntResource");
            }
        }

        @Override
        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an OntResource facet if it is a uri or bnode
            return node.isURI() || node.isBlank();
        }
    };
    
	public HIFResourceImp(Node n, EnhGraph g) {
		super(n, g);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMostSpecificClasses() {
		// TODO Auto-generated method stub
		System.out.println("test");
		return 1;
	}

}
