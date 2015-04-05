package subtree;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by anie on 3/6/2015.
 */
public class VarroXMLHandler extends DefaultHandler {

    boolean negativeCount = false;
    int relativeFactor = 1;

    String tree = "";
    String rootCount = "";

    //subtree -> rel_value
    Map<String, Double> subtreeList = new HashMap<>();
    //subtree -> sentences
    Map<String, ArrayList<String>> subtreeSentenceMap = new HashMap<>();
    Map<String, Integer> sentences = new HashMap<>();

    boolean inTree = false;
    boolean inAddr = false;

    ArrayList<String> addresses = new ArrayList<>();

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {


        if (qName.equalsIgnoreCase("subtree")) {
            rootCount = attributes.getValue("rootCount");
        }

        if (qName.equalsIgnoreCase("tree")) {
            inTree = true;
        }

        if (qName.equalsIgnoreCase("addresses")) {
            inAddr = true;
        }

        if (qName.equalsIgnoreCase("node")) {
            //2 kinds of nodes: subtree node, and address node,
            //differentiable by attributes
            if (attributes.getValue("label") != null && inTree) {
                tree += "(" + attributes.getValue("label") + " ";
            }
            else if (attributes.getValue("id") != null && inAddr) {
                String[] pieces = attributes.getValue("id").split(":");
                addresses.add(pieces[0] + "_" + pieces[1]);
                sentences.putIfAbsent(pieces[0] + "_" + pieces[1], 0);
            }
        }

    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName.equalsIgnoreCase("tree")) {
            tree += ")";
            double tempRoot = Double.parseDouble(rootCount);

            if (negativeCount)
                tempRoot = -tempRoot;

            final double root = tempRoot / relativeFactor;

            //if we already have the tree inside
            subtreeList.computeIfPresent(tree, (string, val) -> val + root);
            subtreeList.putIfAbsent(tree, root);
            inTree = false;
        }

        if (qName.equalsIgnoreCase("addresses")) {
            inAddr = false;
        }

        //close parentheses for tree
        if (qName.equalsIgnoreCase("node")) {
            if (inTree)
                tree += ")";
        }

        //save tree, rootCount to hashmap
        if (qName.equalsIgnoreCase("subtree")) {
            subtreeSentenceMap.put(tree, addresses);
            tree = "";
            addresses = new ArrayList<String>();
        }

    }

}
