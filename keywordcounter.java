//Nick Kroeger

import java.io.*;
import java.util.*;

public class keywordcounter{

    //helper functions to help with code readability
    static boolean firstStringIsKeyword(String firstString){
        return firstString.charAt(0) == '$';
    }
    static boolean firstStringIsNumber(String firstString){
        return firstString.charAt(0) != '$';
    }
    static boolean firstStringSaysStop(String firstString){
        return firstString.equals("stop");
    }

    //helper function to help with code readability
    static void increaseWordFrequency(HashMap<String,Node> hashMap, FibonacciHeap fibHeap, String firstString, int keywordFreq){
        int increaseKey = hashMap.get(firstString).getFrequency() + keywordFreq;
        fibHeap.increaseKey(hashMap.get(firstString),increaseKey);
    }

    static void writeTopNkeywords(HashMap<String,Node> hashMap, FibonacciHeap fibHeap, String firstString, BufferedWriter writer) throws Exception{
        //when query is called, print the keywords corresponding to the top N frequencies
        int numToPrint = Integer.parseInt(firstString);
        //Removed Nodes
        ArrayList<Node> nodesToPrint = new ArrayList<Node>(numToPrint);
        for(int i = 0; i < numToPrint; i++){// remove max N times
            Node maxNode = fibHeap.removeMax();
            hashMap.remove(maxNode.getKeyword());
            Node newNode = new Node(maxNode.getKeyword(), maxNode.getFrequency());
            nodesToPrint.add(newNode);
            //formating with a comma
            if(i == numToPrint - 1)
                writer.write(maxNode.getKeyword());
            else
                writer.write(maxNode.getKeyword() + ",");
        }
        //don't want to actually remove them just because we are printing. We need to put them back!
        for(Node n : nodesToPrint){
            fibHeap.insert(n);
            hashMap.put(n.getKeyword(), n);
        }
        //write to output_file.txt
        writer.newLine();
    }

    public static void main(String[] args) throws Exception{
        String fileName = args[0];
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        //Hash Map for Storing the hashTag and the node
        HashMap<String,Node> hashMap = new HashMap();
        //Create an object of the fibonacci Heap
        FibonacciHeap fibHeap = new FibonacciHeap();

        File outFile = new File("output_file.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        String lineInput;
        String[] splitLineInput;
        String firstString;
        String keyword;
        int keywordFreq;
        int numToPrint;
        while ((lineInput = br.readLine()) != null){
            //read in each line of input from the input file
            splitLineInput = lineInput.split(" "); //$facebook 1 or 3 or stop
            firstString = splitLineInput[0];

            if(firstStringSaysStop(firstString))//ex: stop
                break;
            else if(firstStringIsKeyword(firstString)){//ex: $facebook 1
                keyword = firstString.substring(1);
                keywordFreq = Integer.parseInt(splitLineInput[1]);

                if (hashMap.containsKey(keyword))//increase key since it's already in the heap
                    increaseWordFrequency(hashMap, fibHeap, keyword, keywordFreq);
                else{
                    //Create new node and insert in fibonacci heap and hash map
                    Node newNode = new Node(keyword, keywordFreq);
                    hashMap.put(keyword,newNode);
                    fibHeap.insert(newNode);
                }
            }
            else if(firstStringIsNumber(firstString))// ex: 3
                writeTopNkeywords(hashMap, fibHeap, firstString, writer);//Number of Nodes to be removed
        }
        writer.close();
    }

}
