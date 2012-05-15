/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recommender;
import bdb.MyBDB;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import newsRanking.Ranking;
import nlp.StanfordNLP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author XZH
 */
public class Recommender {
    static MyBDB myBDB = MyBDB.getBDB();
    
    public static Set<String> getRecommendation(String userId, Map<String, JSONObject> candidateItems)
    {
        Set<String> markeditems = myBDB.getMarkedItems(userId);
        for(String item : markeditems)
            candidateItems.remove(item);
        Map<String, Map<String,Double>> candidates = transformCandidates(candidateItems);
        return Ranking.selectBestSet(candidates, myBDB.getSelfProfile(userId), 5);
    }
    
    private static Map<String, Map<String,Double>> transformCandidates(Map<String, JSONObject> candidateItems)
    {
        Map<String, Map<String,Double>> candidates = new HashMap<String, Map<String,Double>>();
        for(Map.Entry<String, JSONObject> e : candidateItems.entrySet())
        {
            candidates.put(e.getKey(), processJSONItem(e.getValue()));
        }
        return candidates;
    }
    
    private static Map<String,Double> processJSONItem(JSONObject item)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(((JSONObject)(item.get("body"))).get("text").toString());
        JSONArray comments = (JSONArray)(((JSONObject)item.get("comments")).get("comments"));
        for(Object o : comments)
        {
            sb.append(((JSONObject)o).get("body").toString());
        }
        Map<String,Double> allProfile = new HashMap<String,Double>();
        Map<String,Double> nerProfile = new HashMap<String,Double>();
        StanfordNLP.process(sb.toString(), allProfile, nerProfile);
        return allProfile;
    }
}
