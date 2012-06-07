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
    static int size = 15;

    public static void main(String[] args) {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            Set<String> allUsers = bdb.getAllUserId();
            int countInteresting = 0;
            int countBoring = 0;
            for (String uid : allUsers) {
                Set<String> i = bdb.getInterestingItems(uid);
                Set<String> b = bdb.getBoringItems(uid);
                Set<String> d = bdb.getDontKnowItems(uid);
                if(i.size() + b.size() < 30) continue;
                System.out.println("user:" + uid + " interesting:" + i.size() + " boring:" + b.size() + " dontknow(skip):" + d.size());
                Map<String, Map<String, Double>> candidates = getCandidates(uid, bdb);
                testTopK(uid, bdb, candidates);
                //testSetSimilarity(uid,bdb,candidates);
            }

            bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Map<String, Map<String, Double>> getCandidates(String uid, MyBDB bdb) {
        Set<String> markedItems = bdb.getMarkedItems(uid);
        Map<String, Map<String, Double>> candidates = new HashMap<String, Map<String, Double>>();

        for (String itemId : markedItems) {
            candidates.put(itemId, bdb.getItem(itemId));
        }
        return candidates;
    }

    private static void testTopK(String uid, MyBDB bdb, Map<String, Map<String, Double>> candidates) {
        Set<String> interesting = bdb.getInterestingItems(uid);
        Set<String> boring = bdb.getBoringItems(uid);
        int count = 0;

        Map<String, Double> strongProfile = bdb.getStrongProfile(uid);
        Set<String> selectBestSet1 = Ranking.selectTopK(candidates, strongProfile, size);
        for (String id : selectBestSet1) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("strong:"+count);

        count = 0;
        Map<String, Double> weakProfile = bdb.getWeakProfile(uid);
        Set<String> selectBestSet2 = Ranking.selectTopK(candidates, weakProfile, size);
        for (String id : selectBestSet2) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("weak:"+count);
        
        count = 0;
        Map<String, Double> selfProfile = bdb.getSelfProfile(uid);
        Set<String> selectBestSet3 = Ranking.selectTopK(candidates, selfProfile, size);
        for (String id : selectBestSet3) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("self:"+count);

        count = 0;
        Map<String, Double> followeeProfile = bdb.getFolloweeProfile(uid);
        Set<String> selectBestSet4 = Ranking.selectTopK(candidates, followeeProfile, size);
        for (String id : selectBestSet4) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("followee:"+count);
        System.out.println("==============================================================");
    }
    
     private static void testSetSimilarity(String uid, MyBDB bdb, Map<String, Map<String, Double>> candidates) {
        Set<String> interesting = bdb.getInterestingItems(uid);
        Set<String> boring = bdb.getBoringItems(uid);
        int count = 0;
        int size = 10;

        Map<String, Double> strongProfile = bdb.getStrongProfile(uid);
        Set<String> selectBestSet1 = Ranking.selectBestSet(candidates, strongProfile, size);
        for (String id : selectBestSet1) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("strong:"+count);

        count = 0;
        Map<String, Double> weakProfile = bdb.getWeakProfile(uid);
        Set<String> selectBestSet2 = Ranking.selectBestSet(candidates, weakProfile, size);
        for (String id : selectBestSet2) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("weak:"+count);
        
        count = 0;
        Map<String, Double> selfProfile = bdb.getSelfProfile(uid);
        Set<String> selectBestSet3 = Ranking.selectBestSet(candidates, selfProfile, size);
        for (String id : selectBestSet3) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("self:"+count);

        count = 0;
        Map<String, Double> followeeProfile = bdb.getFolloweeProfile(uid);
        Set<String> selectBestSet4 = Ranking.selectBestSet(candidates, followeeProfile, size);
        for (String id : selectBestSet4) {
            if (interesting.contains(id)) {
                count++;
            }
        }
        System.out.println("followee:"+count);
        System.out.println("==============================================================");
    }

    public static void checkOverlap() {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";

            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String, Double>> candidates = new HashMap<String, Map<String, Double>>();

            for (String itemId : markedItems) {
                candidates.put(itemId, bdb.getItem(itemId));
            }

            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            System.out.println(interesting.size());
            System.out.println(boring.size());
            int count = 0;

            int size = 15;

            Map<String, Double> strongProfile = bdb.getStrongProfile(Joel);
            Set<String> selectBestSet1 = Ranking.selectTopK(candidates, strongProfile, size);
            for (String id : selectBestSet1) {
                if (interesting.contains(id)) {
                    count++;
                }
            }
            System.out.println(count);

            count = 0;
            Map<String, Double> weakProfile = bdb.getWeakProfile(Joel);
            Set<String> selectBestSet2 = Ranking.selectTopK(candidates, weakProfile, size);
            for (String id : selectBestSet2) {
                if (interesting.contains(id)) {
                    count++;
                }
            }
            System.out.println(count);

            count = 0;
            Map<String, Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            Set<String> selectBestSet3 = Ranking.selectTopK(candidates, followeeProfile, size);
            for (String id : selectBestSet3) {
                if (interesting.contains(id)) {
                    count++;
                }
            }
            System.out.println(count);

            count = 0;
            Set<String> union = new TreeSet<String>();
            union.addAll(selectBestSet1);
            union.addAll(selectBestSet3);
            for (String id : union) {
                if (interesting.contains(id)) {
                    count++;
                }
            }
            System.out.println(count);


            bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void oneTimeSelectionTopK() {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";

            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String, Double>> candidates = new HashMap<String, Map<String, Double>>();

            for (String itemId : markedItems) {
                candidates.put(itemId, bdb.getItem(itemId));
            }

            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            Map<String, Double> selfProfile;
            Set<String> selectBestSet;
            int size = 15;

            selfProfile = bdb.getSelfProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, selfProfile, size);
            int countInteresting = 0;
            int countBoring = 0;
            for (String id : selectBestSet) {
                if (interesting.contains(id)) {
                    countInteresting++;
                }
                if (boring.contains(id)) {
                    countBoring++;
                }
            }
            System.out.println("SelfProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);


            Map<String, Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, followeeProfile, size);
            countInteresting = 0;
            countBoring = 0;
            for (String id : selectBestSet) {
                if (interesting.contains(id)) {
                    countInteresting++;
                }
                if (boring.contains(id)) {
                    countBoring++;
                }
            }
            System.out.println("FolloweeProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);

            Map<String, Double> strongProfile = bdb.getStrongProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, strongProfile, size);
            countInteresting = 0;
            countBoring = 0;
            for (String id : selectBestSet) {
                if (interesting.contains(id)) {
                    countInteresting++;
                }
                if (boring.contains(id)) {
                    countBoring++;
                }
            }
            System.out.println("StrongProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);

            Map<String, Double> weakProfile = bdb.getWeakProfile(Joel);
            selectBestSet = Ranking.selectTopK(candidates, weakProfile, size);
            countInteresting = 0;
            countBoring = 0;
            for (String id : selectBestSet) {
                if (interesting.contains(id)) {
                    countInteresting++;
                }
                if (boring.contains(id)) {
                    countBoring++;
                }
            }
            System.out.println("WeakProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);

            bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void multipleTimeSetSelection() {
        try {
            MyBDB bdb = MyBDB.openBDB("/home/zxu/apache-tomcat-7.0.27/data/BerkeleyDB");
            //get the old items
            String Joel = "00590000000DmMKAA0";

            Set<String> markedItems = bdb.getMarkedItems(Joel);
            Map<String, Map<String, Double>> originalCandidates = new HashMap<String, Map<String, Double>>();

            for (String itemId : markedItems) {
                originalCandidates.put(itemId, bdb.getItem(itemId));
            }

            //run recommendation using different profiles and see the result
            Set<String> interesting = bdb.getInterestingItems(Joel);
            Set<String> boring = bdb.getBoringItems(Joel);
            Set<String> selectBestSet;
            Map<String, Map<String, Double>> tempCandidates;
            int size = 15;
            int countInteresting = 0;
            int countBoring = 0;

            Map<String, Double> selfProfile = bdb.getSelfProfile(Joel);
            tempCandidates = new HashMap<String, Map<String, Double>>(originalCandidates);
            while (tempCandidates.size() >= size) {
                selectBestSet = Ranking.selectBestSet(tempCandidates, selfProfile, size);
                countInteresting = 0;
                countBoring = 0;
                for (String id : selectBestSet) {
                    if (interesting.contains(id)) {
                        countInteresting++;
                    }
                    if (boring.contains(id)) {
                        countBoring++;
                    }
                }
                System.out.println("SelfProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }


            Map<String, Double> followeeProfile = bdb.getFolloweeProfile(Joel);
            tempCandidates = new HashMap<String, Map<String, Double>>(originalCandidates);
            while (tempCandidates.size() >= size) {
                selectBestSet = Ranking.selectBestSet(tempCandidates, followeeProfile, size);
                countInteresting = 0;
                countBoring = 0;
                for (String id : selectBestSet) {
                    if (interesting.contains(id)) {
                        countInteresting++;
                    }
                    if (boring.contains(id)) {
                        countBoring++;
                    }
                }
                System.out.println("FolloweeProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }

            Map<String, Double> strongProfile = bdb.getStrongProfile(Joel);
            tempCandidates = new HashMap<String, Map<String, Double>>(originalCandidates);
            while (tempCandidates.size() >= size) {
                selectBestSet = Ranking.selectBestSet(tempCandidates, strongProfile, size);
                countInteresting = 0;
                countBoring = 0;
                for (String id : selectBestSet) {
                    if (interesting.contains(id)) {
                        countInteresting++;
                    }
                    if (boring.contains(id)) {
                        countBoring++;
                    }
                }
                System.out.println("StrongProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }

            Map<String, Double> weakProfile = bdb.getWeakProfile(Joel);
            tempCandidates = new HashMap<String, Map<String, Double>>(originalCandidates);
            while (tempCandidates.size() >= size) {
                selectBestSet = Ranking.selectBestSet(tempCandidates, weakProfile, size);
                countInteresting = 0;
                countBoring = 0;
                for (String id : selectBestSet) {
                    if (interesting.contains(id)) {
                        countInteresting++;
                    }
                    if (boring.contains(id)) {
                        countBoring++;
                    }
                }
                System.out.println("WeakProfile: Interesting=" + countInteresting + ", Boring=" + countBoring);
                tempCandidates.keySet().removeAll(selectBestSet);
            }

            bdb.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
