//package edu.nyu.bigdata.summary;

import java.util.*;
import java.lang.Math;

public class Sentence {
    private static final double K_CONSTANT = 1.2;
    private static final double B_CONSTANT = 0.75;
    
    private final String sentence;
    private final int length;
    private Map<String, Integer> frequency;
    private double bm25val;
    // TODO: there's a better way to do this right?        
    private ArrayList<String> currentSentence = new ArrayList<String>();
    public Sentence(String s) {
        sentence = s;
        frequency = new HashMap<String, Integer>();
        bm25val = -1;
        // remove punctuation and convert to lowercase and then split
        // on white spaces
        String[] words = s.replaceAll("[^a-zA-Z ]", "")
            .toLowerCase().split("\\s+");

        length = words.length;
        
        for(String singleWord: words){
            if(frequency.containsKey(singleWord)){
                int count = frequency.get(singleWord) + 1;
                frequency.put(singleWord, count);
            } else {
                frequency.put(singleWord, 1);
            }
            if (!currentSentence.contains(singleWord)) {
                currentSentence.add(singleWord);
            }
        }
    }
    
    public String getSentence() {
        return sentence;
    }
    
    public int getLength() {
        return length;
    }
    
    public Map<String, Integer> getFrequency() {
        return frequency;
    }

    public ArrayList<String> getCurrentSentence(){
        return currentSentence;
    }
    
    private double calculateK(double averageLength) {
        double result = (double)getLength() / averageLength;
        result = B_CONSTANT * result;
        result += (1 - B_CONSTANT);
        return K_CONSTANT * result;
    }
    public double calculateBM25(double averageLength, 
                                Map<String, Integer> termOccurence,
                                int sentenceNum) {
        double K = calculateK(averageLength);
        double result = 0;
        
        for(Map.Entry<String, Integer> entry : getFrequency().entrySet()){
            String word = entry.getKey();

            double logSide = sentenceNum - termOccurence.get(word) + 0.5;
            logSide = logSide / (termOccurence.get(word) + 0.5);
            logSide = Math.log(logSide);
            
            double kSide = (K_CONSTANT + 1) * entry.getValue();
            kSide = kSide / (K + entry.getValue());
            
            result += (logSide * kSide);
        }   
        bm25val = result;
        return result;
    }
    
    public double getSimilarity(Sentence other) {
        double count = 0;
        Map<String, Integer> otherFrequency = other.getFrequency();
        
        for(Map.Entry<String, Integer> entry : getFrequency().entrySet()){
            if(otherFrequency.containsKey(entry.getKey())){
                int otherCount = otherFrequency.get(entry.getKey());
                count += (otherCount < entry.getValue() ?
                    otherCount : entry.getValue());
            }
        }
        
        return (count / getLength());
    }
    
    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        Sentence obj = (Sentence) o;
        
        if (getLength() != obj.getLength()) {
            return false;
        }
        
        if (!getSentence().equals(obj.getSentence())) {
            return false;
        }
        
        return getFrequency().equals(obj.getFrequency());
    }
    
    @Override public int hashCode() {
        int hash = (getSentence() == null) ?
            0 : getSentence().hashCode();
            
        hash = 31 * hash + (Integer.valueOf(getLength()) == null ?
            0 : Integer.valueOf(getLength()).hashCode());
            
        hash = 31 * hash + (getFrequency() == null ?
            0 : getFrequency().hashCode());
            
        return hash;
    }
}
