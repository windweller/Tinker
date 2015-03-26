package fileIO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CSVHandler {

    public static ArrayList<String> parseLine(String line) {

        ArrayList<String> result = new ArrayList<String>();

        try {
            CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
            for (CSVRecord record : parser) {
                for (String aRecord : record) {
                    result.add(aRecord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void writeLine(ArrayList<String> parts, FileWriter fw) throws IOException {
        CSVPrinter csvFilePrinter = new CSVPrinter(fw, CSVFormat.RFC4180);
        csvFilePrinter.printRecord(parts);
        fw.flush();
        csvFilePrinter.close();
    }


}
