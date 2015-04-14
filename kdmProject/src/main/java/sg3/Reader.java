package sg3;

import java.util.Set;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class Reader {
    static final String owlFile = "https://raw.githubusercontent.com/mtrzepka/cs560/master/sickOntology.owl";

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final OntModel m = ModelFactory.createOntologyModel();
        m.read(owlFile);

        System.out.println("Object Properties:\n");
        final Set<ObjectProperty> properties = m.listObjectProperties().toSet();

        for (final ObjectProperty property : properties) {
            System.out.println(property.getLocalName());
        }

        System.out.println("\nWords:");

        final Set<OntClass> words = m.listClasses().toSet();
        for (final OntClass word : words) {
            System.out.println("\n" + word.getLocalName() + "\n");
            for (final ObjectProperty property : properties) {
                for (final Statement relation : word.listProperties(property).toSet()) {
                    System.out.println(property.getLocalName() + " : " + relation.getString());
                }
            }
        }

        // System.out.println();

        // m.write(System.out);
    }
}
