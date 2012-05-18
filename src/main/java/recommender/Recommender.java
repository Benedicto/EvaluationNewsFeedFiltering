/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recommender;
import bdb.MyBDB;
import java.util.*;
import newsRanking.Ranking;
import nlp.NLP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author XZH
 */
public class Recommender {
    public static Set<String> getRecommendation(String userId, Map<String, JSONObject> candidateItems)
    {
        Set<String> markeditems = MyBDB.getBDB().getMarkedItems(userId);
        for(String item : markeditems)
            candidateItems.remove(item);
        Map<String, Map<String,Double>> candidatesALL = new HashMap<String, Map<String,Double>>();
        Map<String, Map<String,Double>> candidatesNER = new HashMap<String, Map<String,Double>>();
        
        System.out.println("nlp processing for items start: " + new Date());
        transformCandidates(candidateItems,candidatesALL,candidatesNER);
        System.out.println("nlp processing for items end: " + new Date());
        
        //choose NER or all words
        Map<String, Map<String,Double>> candidates;
        boolean useNER = (Math.random() < 0.5);
        if(useNER)
            candidates = candidatesNER;
        else
            candidates = candidatesALL;
        
        //choose which profile to use
        Map<String, Double> profile = chooseProfile(userId, useNER);
        
        System.out.println("select items start: " + new Date());
        Set<String> result = Ranking.selectBestSet(candidates, profile, 5);
        System.out.println("select items end: " + new Date());
        return result;
    }
    
    private static Map<String, Double> chooseProfile(String userId, boolean useNER) {
        Map<String, Double> profile = null;
        int choice = (int) (Math.random() * 8); //choice will be one of {0,1,2,3,4,5,6,7}
        
        choice = 4; // demo stage, always use selfProfile
        
        if (choice > 3) {
            if (useNER) {
                profile = MyBDB.getBDB().getSelfProfileNER(userId);
            } else {
                profile = MyBDB.getBDB().getSelfProfile(userId);
            }
        }
        if (choice <= 3) {
            if (useNER) {
                profile = MyBDB.getBDB().getFolloweeProfileNER(userId);
            } else {
                profile = MyBDB.getBDB().getFolloweeProfile(userId);
            }
        }
        return profile;
    }
    
    private static void transformCandidates(
            Map<String, JSONObject> candidateItems,
            Map<String, Map<String,Double>> candidatesALL,
            Map<String, Map<String,Double>> candidatesNER)
    {
        for(Map.Entry<String, JSONObject> e : candidateItems.entrySet())
        {
            Map<String,Double> all = new HashMap<String,Double>();
            Map<String,Double> ner = new HashMap<String,Double>();
            processJSONItem(e.getValue(), all, ner);
            candidatesALL.put(e.getKey(), all);
            candidatesNER.put(e.getKey(), ner);
        }
    }
    
    private static void processJSONItem(JSONObject item, Map<String,Double> all, Map<String,Double> ner)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(((JSONObject)(item.get("body"))).get("text").toString());
        JSONArray comments = (JSONArray)(((JSONObject)item.get("comments")).get("comments"));
        for(Object o : comments)
        {
            sb.append(((JSONObject)o).get("body").toString());
        }
        all = new HashMap<String,Double>();
        ner = new HashMap<String,Double>();
        NLP.process(sb.toString(), all, ner);
    }
}
