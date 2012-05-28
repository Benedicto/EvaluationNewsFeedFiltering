/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import bdb.MyBDB;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import newsRanking.Ranking;

/**
 *
 * @author zxu
 */
public class AnalyzeData {
    public static void main(String[] args)
    {
        checkOverlap();
        
    }
    
    public static void checkOverlap()
    {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";
            
            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String,Double>> candidates = new HashMap<String, Map<String,Double>>();
                    
            for(String itemId : markedItems)
            {
                candidates.put(itemId, bdb.getItem(itemId));
            }
                    
            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            System.out.println(interesting.size());
            System.out.println(boring.size());
            int count=0;

            int size=15;
            
            Map<String,Double> strongProfile = bdb.getStrongProfile(Joel);
            Set<String> selectBestSet1 = Ranking.selectTopK(candidates, strongProfile, size);
            for(String id : selectBestSet1)
            {
                if(interesting.contains(id))
                    count++;
            }
            System.out.println(count);
            
            count=0;
            Map<String,Double> weakProfile = bdb.getWeakProfile(Joel);
            Set<String> selectBestSet2 = Ranking.selectTopK(candidates, weakProfile, size);
            for(String id : selectBestSet2)
            {
                if(interesting.contains(id))
                    count++;
            }
            System.out.println(count);
            
            count=0;
            Map<String,Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            Set<String> selectBestSet3 = Ranking.selectTopK(candidates, followeeProfile, size);
            for(String id : selectBestSet3)
            {
                if(interesting.contains(id))
                    count++;
            }
            System.out.println(count);
            
            count=0;
            Set<String> union = new TreeSet<String>();
            union.addAll(selectBestSet1);
            union.addAll(selectBestSet3);
            for(String id : union)
            {
                if(interesting.contains(id))
                    count++;
            }
            System.out.println(count);

            
            bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void oneTimeSelectionTopK()
    {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";
            
            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String,Double>> candidates = new HashMap<String, Map<String,Double>>();
                    
            for(String itemId : markedItems)
            {
                candidates.put(itemId, bdb.getItem(itemId));
            }
                    
            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            Map<String,Double> selfProfile;
            Set<String> selectBestSet;
            int size=15;
            
            selfProfile = bdb.getSelfProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, selfProfile, size);
            int countInteresting=0;
            int countBoring=0;
            for(String id : selectBestSet)
            {
                if(interesting.contains(id)) countInteresting++;
                if(boring.contains(id)) countBoring++;
            }
            System.out.println("SelfProfile: Interesting="+countInteresting+", Boring="+countBoring);
            
            
            Map<String,Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, followeeProfile, size);
            countInteresting=0;
            countBoring=0;
            for(String id : selectBestSet)
            {
                if(interesting.contains(id)) countInteresting++;
                if(boring.contains(id)) countBoring++;
            }
            System.out.println("FolloweeProfile: Interesting="+countInteresting+", Boring="+countBoring);
            
            Map<String,Double> strongProfile = bdb.getStrongProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, strongProfile, size);
            countInteresting=0;
            countBoring=0;
            for(String id : selectBestSet)
            {
                if(interesting.contains(id)) countInteresting++;
                if(boring.contains(id)) countBoring++;
            }
            System.out.println("StrongProfile: Interesting="+countInteresting+", Boring="+countBoring);
            
            Map<String,Double> weakProfile = bdb.getWeakProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, weakProfile, size);
            countInteresting=0;
            countBoring=0;
            for(String id : selectBestSet)
            {
                if(interesting.contains(id)) countInteresting++;
                if(boring.contains(id)) countBoring++;
            }
            System.out.println("WeakProfile: Interesting="+countInteresting+", Boring="+countBoring); 
            
            bdb.close();            
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public static void multipleTimeSetSelection()
    {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";
            
            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String,Double>> originalCandidates = new HashMap<String, Map<String,Double>>();
                    
            for(String itemId : markedItems)
            {
                originalCandidates.put(itemId, bdb.getItem(itemId));
            }
                    
            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            Set<String> selectBestSet;
            Map<String, Map<String,Double>> tempCandidates;
            int size=15;
            int countInteresting=0;
            int countBoring=0;
            
            Map<String,Double> selfProfile = bdb.getSelfProfile(Joel);
            tempCandidates = new HashMap<String, Map<String,Double>>(originalCandidates);
            while(tempCandidates.size() >= size)
            {
                selectBestSet = Ranking.selectBestSet(tempCandidates, selfProfile, size);
                countInteresting=0;
                countBoring=0;
                for(String id : selectBestSet)
                {
                    if(interesting.contains(id)) countInteresting++;
                    if(boring.contains(id)) countBoring++;
                }
                System.out.println("SelfProfile: Interesting="+countInteresting+", Boring="+countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }
            
            
            Map<String,Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            tempCandidates = new HashMap<String, Map<String,Double>>(originalCandidates);
            while(tempCandidates.size() >= size)
            {
                selectBestSet = Ranking.selectBestSet(tempCandidates, followeeProfile, size);
                countInteresting=0;
                countBoring=0;
                for(String id : selectBestSet)
                {
                    if(interesting.contains(id)) countInteresting++;
                    if(boring.contains(id)) countBoring++;
                }
                System.out.println("FolloweeProfile: Interesting="+countInteresting+", Boring="+countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }
            
            Map<String,Double> strongProfile = bdb.getStrongProfile(Joel);
            tempCandidates = new HashMap<String, Map<String,Double>>(originalCandidates);
            while(tempCandidates.size() >= size)
            {
                selectBestSet = Ranking.selectBestSet(tempCandidates, strongProfile, size);
                countInteresting=0;
                countBoring=0;
                for(String id : selectBestSet)
                {
                    if(interesting.contains(id)) countInteresting++;
                    if(boring.contains(id)) countBoring++;
                }
                System.out.println("StrongProfile: Interesting="+countInteresting+", Boring="+countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }
            
            Map<String,Double> weakProfile = bdb.getWeakProfile(Joel);
            tempCandidates = new HashMap<String, Map<String,Double>>(originalCandidates);
            while(tempCandidates.size() >= size)
            {
                selectBestSet = Ranking.selectBestSet(tempCandidates, weakProfile, size);
                countInteresting=0;
                countBoring=0;
                for(String id : selectBestSet)
                {
                    if(interesting.contains(id)) countInteresting++;
                    if(boring.contains(id)) countBoring++;
                }
                System.out.println("WeakProfile: Interesting="+countInteresting+", Boring="+countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }
            
             bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     public static void transferData()
    {
        try {
            String newBDB = "/home/zxu/NewsFeedRanking/data/BerkeleyDB";
            String oldBDB = "/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB";
            
            MyBDB bdb = MyBDB.openBDB(oldBDB);
            String Joel = "00590000000DmMKAA0";
            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String,Double>> itemContents = new HashMap<String, Map<String,Double>>();                    
            for(String itemId : markedItems)
            {
                itemContents.put(itemId, bdb.getItem(itemId));
            }
                    
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            
            bdb.close();
            
            bdb = MyBDB.openBDB(newBDB);
            for(String item : interesting)
            {
                bdb.putInteresting(Joel, item);
            }
            for(String item : boring)
            {
                bdb.putBoring(Joel, item);
            }
            for(String item : markedItems)
            {
                bdb.putItems(item, itemContents.get(item));
            }
            
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
