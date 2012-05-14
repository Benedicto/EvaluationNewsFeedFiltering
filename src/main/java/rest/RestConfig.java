/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

/**
 *
 * @author zxu
 */
public class RestConfig {
    private final String client_id;
    private final String redirect_uri;
    private final String client_secret;
    private final String tokenURL;
    private final String authURL;
    
    private RestConfig(RESTAppBuilder builder)
    {
        this.client_id = builder.client_id;
        this.redirect_uri = builder.redirect_uri;
        this.client_secret = builder.client_secret;
        this.tokenURL = builder.tokenURL;
        this.authURL = builder.authURL;
    }

    /**
     * @return the client_id
     */
    public String getClient_id() {
        return client_id;
    }

    /**
     * @return the redirect_url
     */
    public String getRedirect_uri() {
        return redirect_uri;
    }

    /**
     * @return the client_secret
     */
    public String getClient_secret() {
        return client_secret;
    }

    /**
     * @return the tokenURL
     */
    public String getTokenURL() {
        return tokenURL;
    }

    /**
     * @return the authURL
     */
    public String getAuthURL() {
        return authURL;
    }
    
    public static class RESTAppBuilder
    {
        private String client_id;
        private String redirect_uri;
        private String client_secret;
        private String tokenURL;
        private String authURL;
        
        public RESTAppBuilder()
        {
            
        }
        public RESTAppBuilder client_id(String client_id)
        {
            this.client_id = client_id;
            return this;
        }
        public RESTAppBuilder redirect_uri(String redirect_uri)
        {
            this.redirect_uri = redirect_uri;
            return this;
        }
        public RESTAppBuilder client_secret(String client_secret)
        {
            this.client_secret = client_secret;
            return this;
        }
        public RESTAppBuilder tokenURL(String tokenURL)
        {
            this.tokenURL = tokenURL;
            return this;
        }
        public RESTAppBuilder authURL(String authURL)
        {
            this.authURL = authURL;
            return this;
        }
        public RestConfig build()
        {
            return new RestConfig(this);
        }
    }
    
    public static final RestConfig developerForceREST = new RESTAppBuilder().client_id("3MVG9rFJvQRVOvk6sl7xMXtrbya4bOKG3sXQYU7cevDtXtX0jrtRwA1PvF9pXdSwbVOV9PrtMHSBZJfAQrKuo").redirect_uri("https://localhost:8443/EvaluationNewsFeedFiltering/home.jsp").client_secret("8020070739192617674").tokenURL("https://login.salesforce.com/services/oauth2/token").authURL("https://login.salesforce.com/services/oauth2/authorize").build();
    public static final RestConfig gusREST = new RESTAppBuilder().client_id("3MVG92.uWdyphVj6UxPnhEXcGWPpVRheELsx9An.CZdCuUVSiepPic6SwD6b59JXDtrfbkCkJRDtQZJc_n6dN").redirect_uri("https://10.132.35.240:8443/EvaluationNewsFeedFiltering/").client_secret("9014937268783936461").tokenURL("https://gus.salesforce.com/services/oauth2/token").authURL("https://gus.salesforce.com/services/oauth2/authorize").build();
    public static final RestConfig currentREST = developerForceREST;
}
