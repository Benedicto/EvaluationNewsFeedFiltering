/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import static rest.RestConfig.currentREST;

/**
 *
 * @author XZH
 */
public class Rest {

    static String client_id = currentREST.getClient_id();
    static String redirect_uri = currentREST.getRedirect_uri();
    static String client_secret = currentREST.getClient_secret();
    static String tokenURL = currentREST.getTokenURL();
    static HttpClient httpclient = new DefaultHttpClient();
    final long oneWeek = 604800000L;
    long timeFrame = oneWeek * 2;
    static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    long timeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(int weeks) {
        timeFrame = oneWeek * weeks;
    }
    String accessToken = null;
    String instanceURL = null;
    String myId = null;

    public Rest(String authcode) {
        try {
            HttpPost post = new HttpPost(tokenURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("client_id", client_id));
            params.add(new BasicNameValuePair("client_secret", client_secret));
            params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            params.add(new BasicNameValuePair("code", authcode));

            post.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse resp = httpclient.execute(post);
            JSONObject json = (JSONObject) JSONValue.parse(new InputStreamReader(resp.getEntity().getContent()));
            accessToken = "OAuth " + (String) json.get("access_token");
            instanceURL = (String) json.get("instance_url");
            String logInId = json.get("id").toString();
            myId = logInId.substring(logInId.indexOf("005"));
        } catch (ClientProtocolException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Rest(String username, String password) {
        try {
            HttpPost post = new HttpPost(tokenURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("client_id", client_id));
            params.add(new BasicNameValuePair("client_secret", client_secret));
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));// + "KOjKGsG6wwdhxdcjO3NO01wph"));

            post.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse resp = httpclient.execute(post);
            JSONObject json = (JSONObject) JSONValue.parse(new InputStreamReader(resp.getEntity().getContent()));
            accessToken = "OAuth " + (String) json.get("access_token");
            instanceURL = (String) json.get("instance_url");
            String logInId = json.get("id").toString();
            myId = logInId.substring(logInId.indexOf("005"));
        } catch (ClientProtocolException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMyId() {
        return myId;
    }

    public void getPhoto(String photoUrl) {
        InputStream is = null;
        try {
            URL url = new URL(photoUrl + "?oauth_token=" + this.getAccessToken());
            //URL url = new URL("https://c.na14.salesforce.com/profilephoto/005/T"+ "?oauth_token=" + this.getAccessToken());
            
            //URL url = new URL("https://www.google.com/logos/2012/d4g12-hp.jpg");//download from goole succeeded.
            URLConnection urlConnection = url.openConnection();
            System.out.println(url.toString());


            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("image.png"));

            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            out.flush();

            out.close();
            in.close();
            
//            HttpGet get = new HttpGet();           
//            get.setHeader("Authorization", accessToken);
//            Object URL = "/services/data/v24.0/chatter/users/me/photo";
//            get.setURI(new URI(this.instanceURL + URL.toString()));
//            HttpResponse response = httpclient.execute(get);
//            JSONObject json = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));
//            System.out.println(json.get("smallPhotoUrl"));
//            System.out.println(json.get("largePhotoUrl"));
        } 
//        catch (URISyntaxException ex) {
//            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
//        } 
        catch (MalformedURLException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, JSONObject> getFeedItems() {
        System.out.println("get items start: " + new Date());
        JSONObject json;
        JSONArray jItems = new JSONArray();
        Map<String, JSONObject> items = new HashMap<String, JSONObject>();
        try {
            HttpGet get = new HttpGet();
            HttpResponse response;
            get.setHeader("Authorization", accessToken);

            Object URL = "/services/data/v24.0/chatter/feeds/news/me/feed-items?pageSize=25";
            long now = new Date().getTime();
            long then;
            do {
                get.setURI(new URI(this.instanceURL + URL.toString()));
                response = httpclient.execute(get);
                json = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));
                jItems.addAll((JSONArray) json.get("items"));
                JSONObject j = (JSONObject) jItems.get(jItems.size() - 1);
                then = Rest.parse(j.get("modifiedDate").toString());
                URL = json.get("nextPageUrl");
            } while (URL != null && now - then < this.timeFrame());

            for (Object o : jItems) {
                JSONObject j = (JSONObject) o;
                then = Rest.parse(j.get("modifiedDate").toString());
                if (now - then < this.timeFrame()) {
                    items.put(j.get("id").toString(), j);
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClientProtocolException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("get items finish: " + new Date());
        System.out.println("number of items: " + items.size());
        return items;
    }
    
    
    //need enough access permission
    public Map<String, JSONObject> getFeedItems(String userId, int weeks) {
        System.out.println("get items start: " + new Date());
        JSONObject json;
        JSONArray jItems = new JSONArray();
        this.setTimeFrame(weeks);
        Map<String, JSONObject> items = new HashMap<String, JSONObject>();
        try {
            HttpGet get = new HttpGet();
            HttpResponse response;
            get.setHeader("Authorization", accessToken);

            Object URL = "/services/data/v24.0/chatter/feeds/news/"+userId+"/feed-items?pageSize=100";
            long now = new Date().getTime();
            long then;
            do {
                get.setURI(new URI(this.instanceURL + URL.toString()));
                response = httpclient.execute(get);
                json = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));              
                jItems.addAll((JSONArray) json.get("items"));
                JSONObject j = (JSONObject) jItems.get(jItems.size() - 1);
                then = Rest.parse(j.get("modifiedDate").toString());
                URL = json.get("nextPageUrl");
            } while (URL != null && now - then < this.timeFrame());

            for (Object o : jItems) {
                JSONObject j = (JSONObject) o;
                then = Rest.parse(j.get("modifiedDate").toString());
                if (now - then < this.timeFrame()) {
                    items.put(j.get("id").toString(), j);
                }
            }

        } catch (ParseException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClientProtocolException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("get items finish: " + new Date());
        System.out.println("number of items: " + items.size());
        return items;
    }
    
    public JSONObject getFeedItem(String itemId) {
        System.out.println("get items start: " + new Date());
        JSONObject json=null;
        JSONArray jarray=null;
        try {
            HttpGet get = new HttpGet();
            HttpResponse response;
            get.setHeader("Authorization", accessToken);

            Object URL = "/services/data/v24.0/chatter/feed-items/"+itemId;
            get.setURI(new URI(this.instanceURL + URL.toString()));
            response = httpclient.execute(get);
            try{
            json = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));
            }catch(java.lang.ClassCastException e)
            {
                response = httpclient.execute(get);
                jarray = (JSONArray) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));
            }

        } catch (URISyntaxException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClientProtocolException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return json;
    }

    private static long parse(String input) throws java.text.ParseException {
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 5) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());
            input = s0 + "GMT" + s1;
        }
        return df.parse(input).getTime();
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
