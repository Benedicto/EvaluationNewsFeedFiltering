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
import rest.Rest.Item;

/**
 *
 * @author XZH
 */
public class Recommender {
    public static Set<String> getRecommendation(String userId, Map<String, JSONObject> candidateItems, int size)
    {
        Set<String> markeditems = MyBDB.getBDB().getMarkedItems(userId);

        
        //remove items already marked by the user
        for(String item : markeditems)
            candidateItems.remove(item);
        
        
        Map<String, Map<String,Double>> candidatesALL = new HashMap<String, Map<String,Double>>();
        Map<String, Map<String,Double>> candidatesNER = new HashMap<String, Map<String,Double>>();
        System.out.println("nlp processing for items start: " + new Date());
        transformCandidates(candidateItems,candidatesALL,candidatesNER);
        System.out.println("nlp processing for items end: " + new Date());
        
        //save the items into BDB for analysis
        for(Map.Entry<String, Map<String,Double>> e: candidatesALL.entrySet())
        {
            MyBDB.getBDB().putItems(e.getKey(), e.getValue());
        }
        
        for(Map.Entry<String, Map<String,Double>> e: candidatesNER.entrySet())
        {
            MyBDB.getBDB().putItemsNER(e.getKey(), e.getValue());
        }       
        
        //choose NER or all words
        Map<String, Map<String,Double>> candidates;
        
        //NER is turned off because it is too slow
        boolean useNER = false;
        if(useNER)
            candidates = candidatesNER;
        else
            candidates = candidatesALL;
        
        //choose which profile to use
        Map<String, Double> profile = chooseProfile(userId, useNER);
        System.out.println("profile size:"+profile.size());
        
        System.out.println("select items start: " + new Date());
        Set<String> result = Ranking.selectBestSet(candidates, profile, size);
        System.out.println("select items end: " + new Date());
        return result;
    }
    
    public static List<Item> getNewest(String userId, LinkedList<Item> candidateItems, int k)
    {
        Set<String> markeditems = MyBDB.getBDB().getMarkedItems(userId);
        List<Item> selected = new LinkedList<Item>();
        
        //remove items already marked by the user
        Iterator<Item> iter = candidateItems.iterator();
        while(iter.hasNext())
        {
            Item next = iter.next();
            if (markeditems.contains(next.id))
            {
                iter.remove();
            }
                
        }
        
        //select the first k items
        iter = candidateItems.iterator();
        while(iter.hasNext() && selected.size() < k)
        {
            selected.add(iter.next());
        }
        
        return selected;
    }
    
    private static Map<String, Double> chooseProfile(String userId, boolean useNER) {
        Map<String, Double> profile = null;
        int choice = (int) (Math.random() * 5); //choice will be one of {0,1,2,3,4}
    
        //right now there is only self profile or followee profile
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
            sb.append(((JSONObject)((JSONObject)o).get("body")).get("text").toString());
        }
        NLP.process(sb.toString(), all, ner);
    }
}
