package sg3;

import rita.RiWordNet;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Main {
    static final String NS = "http://example.com/kdmProject/sg3/";
    static final String WORD_CLASS = NS + "word";

    public static void main(final String[] args) {
        if (args.length > 1 || args.length == 0) {
            System.err.println("Set a single argument for the word you'd like to create an "
                    + "ontology for.");
            System.exit(1);
        }
        final RiWordNet wordnet = new RiWordNet("/usr/local/WordNet-3.0");
        OntModel m = ModelFactory.createOntologyModel();
        final OntClass wordClass = m.createClass(WORD_CLASS);

        // Define the type of relationships

        final ObjectProperty hasSynset = m.createObjectProperty(NS + "hasSynset");
        final ObjectProperty isSynsetOf = m.createObjectProperty(NS + "isSynsetOf");
        final ObjectProperty hasHyponym = m.createObjectProperty(NS + "hasHyponym");
        final ObjectProperty isHyponymOf = m.createObjectProperty(NS + "isHyponymOf");
        final ObjectProperty isSimilarTo = m.createObjectProperty(NS + "isSimilarTo");
        final ObjectProperty hasDerived = m.createObjectProperty(NS + "hasDerived");
        final ObjectProperty isDerivedFrom = m.createObjectProperty(NS + "isDerivedFrom");

        final String word = args[0];
        System.out.println("Word Selected: " + word);

        final String[] posList = wordnet.getPos(word);
        for (final String pos : posList) {
            System.out.println("\nPoS: " + pos);
            final Individual individualWord = m.createIndividual(NS + word + "-" + pos, wordClass);

            final String[] synsets = wordnet.getSynset(word, pos);
            if (synsets.length > 0) {
                System.out.println("\nSynset for " + word + " (pos: " + pos + ")");
                for (final String synset : synsets) {
                    m = createRelation(m, wordClass, word, individualWord, hasSynset, synset, isSynsetOf);
                    System.out.println(synset);
                }
            }

            final String[] hyponyms = wordnet.getAllHyponyms(word, pos);
            if (hyponyms.length > 0) {
                System.out.println("\nHyponyms for " + word + ":");
                for (final String hyponym : hyponyms) {
                    m = createRelation(m, wordClass, word, individualWord, hasHyponym, hyponym, isHyponymOf);
                    System.out.println(hyponym);
                }
            }

            if (pos.equals(RiWordNet.ADJ)) {

                final String[] similarList = wordnet.getSimilar(word, pos);
                if (similarList.length > 0) {
                    System.out.println("\nWords similar to " + word + " (pos: " + pos + ")");
                    for (final String similarStr : similarList) {
                        m = createRelation(m, wordClass, word, individualWord, isSimilarTo, similarStr, isSimilarTo);
                        System.out.println(similarStr);
                    }
                }
            }

            if (pos.equals(RiWordNet.ADV)) {
                final String[] derivedTerms = wordnet.getDerivedTerms(word, pos);
                if (derivedTerms.length > 0) {
                    System.out.println("\nDerived terms for " + word + ":");
                    for (final String derivedTerm : derivedTerms) {
                        m = createRelation(m, wordClass, word, individualWord, hasDerived, derivedTerm, isDerivedFrom);
                        System.out.println(derivedTerm);
                    }
                }
            }
        }

        System.out.println("\n\nRDF Onotology Model: \n");
        m.write(System.out, null);
    }

    private static OntModel createRelation(final OntModel m, final OntClass wordCLass, final String word, final Individual individualWord,
            final ObjectProperty wordProperty, final String relatedWord, final ObjectProperty relatedWordProperty) {
        // add the property to the primary word
        individualWord.addProperty(wordProperty, relatedWord);

        // create the related word
        final Individual relatedIndividualWord = m.createIndividual(NS + relatedWord, wordCLass);
        relatedIndividualWord.addProperty(relatedWordProperty, word);

        return m;
    }
}
