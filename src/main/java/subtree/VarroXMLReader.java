package subtree;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.nio.file.StandardOpenOption;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by anie on 3/6/2015.
 * I will not create classes for the subtrees (too memory consuming)
 */
public class VarroXMLReader {

    SAXParserFactory factory;
    SAXParser saxParser;
    VarroXMLHandler handler;
    public Map<String, ArrayList<String>> ldaSentences = new HashMap<>();

    public Map<String, ArrayList<String>> sentenceVector = new HashMap<>();

    public Map<String, Double> top = new HashMap<>();
    public Map<String, Double> bottom = new HashMap<>();


    public VarroXMLReader() throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();

        handler = new VarroXMLHandler();
    }

    /**
     * It reads in multiple files, keep in mind that
     * odd one is negative, even one is positive file (including 0)
     * @param relFactor this is size of sentence groups!!
     * @param fileAddreses
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    public void parse(int[] relFactor, String... fileAddreses) throws IOException, SAXException {

        int oddeven = 0;

        for (String fileAddr: fileAddreses) {

            if (oddeven % 2 != 0)
                handler.negativeCount = true;

            handler.relativeFactor = relFactor[oddeven];

            File file = new File(fileAddr);
            InputStream inputStream= new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream,"UTF-8");

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            saxParser.parse(is, handler);

            oddeven++;
        }
    }

    //we'll see what's this processing
    //percentage: you will put in how much percentage you want
    //this is on both sides!!
    public void process(double percentage, boolean unilateral) {

        //because I want top 3% and bottom 3%
        int size = (int) Math.floor(handler.subtreeList.size() * percentage);

        Map<String, Double> sorted = handler.subtreeList.entrySet().stream()
                .sorted(Entry.comparingByValue()).collect(Collectors.toMap(Entry::getKey, Map.Entry::getValue));


        Iterator<Entry<String, Double>> it = sorted.entrySet().iterator();
        for (int i = 0; i < size; i++) {
            top.put(it.next().getKey(), it.next().getValue());
        }

        if (!unilateral) {
            for (int i = sorted.size(); i > sorted.size() - size; i--) {
                bottom.put(it.next().getKey(), it.next().getValue());
            }
        }

        generateSentenceFeatureForProcess(top, size, unilateral);
        if (!unilateral) {
            generateSentenceFeatureForProcess(bottom, size, false);
        }

        //then we deal with sentences that are never matched!

        handler.sentences.entrySet().stream().filter(sen -> !sentenceVector.containsKey(sen.getKey()))
                .forEach(sen -> {
                    ArrayList<String> temp = new ArrayList<>(Collections.nCopies(size * 2, "0"));
                    sentenceVector.put(sen.getKey(), temp);
        });

    }

    public void generateSentenceFeatureForProcess(Map<String, Double> map, int size, boolean unilateral) {
        int counter = 0;
        if (unilateral) size *=2;
        for (Entry<String, Double> sm : map.entrySet()) {
            ArrayList<String> sentences = handler.subtreeSentenceMap.get(sm.getKey());

            for (String sentence: sentences) {
                if (sentenceVector.containsKey(sentence)) {
                    sentenceVector.get(sentence).set(counter, "1");
                }
                else {
                    ArrayList<String> temp = new ArrayList<>(Collections.nCopies(size, "0"));
                    temp.set(counter, "1");
                    sentenceVector.put(sentence, temp);
                }
            }
            counter++;
        }
    }

    //percentage: unilateral, you get how much you put in
    //5% means 5% top (instead of 5% top and 5% bottom)
    public void generateLDAFile(double percentage) {

        int size = (int) Math.floor(handler.subtreeList.size() * percentage);

        System.out.println("You are using:" + size + " features!");

        Map<String, Double> sorted = handler.subtreeList.entrySet().stream()
                .sorted(Entry.comparingByValue()).limit(size).collect(Collectors.toMap(Entry::getKey, Map.Entry::getValue));

        for (Entry<String, Double> sm : sorted.entrySet()) {
            ArrayList<String> sentences = handler.subtreeSentenceMap.get(sm.getKey());

            for (String sentence: sentences) {
                if (ldaSentences.containsKey(sentence)) {
                    ldaSentences.get(sentence).add(sm.getKey());
                }
                else {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(sm.getKey());
                    ldaSentences.put(sentence, temp);
                }
            }
        }
    }

    //this is probably not working
    public void printLDAFile(String dir) throws IOException {

        for (Entry<String, ArrayList<String>> sm : ldaSentences.entrySet()) {
            Path path = Paths.get(dir + "\\" + sm.getKey());
            Files.write(path, sm.getValue().stream().reduce((t, u) -> t + "\t" + u).get().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
    }


    public void setNegativeRootCount(boolean value) {
        handler.negativeCount = value;
    }

    public Map<String, Double> getSubtreeList() {
       return handler.subtreeList;
    }

    public  Map<String, ArrayList<String>> getSubtreeSentenceMap() {
        return handler.subtreeSentenceMap;
    }

    public Map<String, Integer> getSentences() {
        return handler.sentences;
    }

}
