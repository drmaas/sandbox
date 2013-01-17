/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package drmaas.sandbox.http;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class LoginTest {

    public static void main(String[] args) throws Exception {

        //1. For SSL
        DefaultHttpClient base = new DefaultHttpClient();
        SSLContext ctx = SSLContext.getInstance("TLS");
        
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }
            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            }
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        
        X509HostnameVerifier verifier = new X509HostnameVerifier() {
         
            @Override
            public void verify(String string, SSLSocket ssls) throws IOException {
            }

            @Override
            public void verify(String string, X509Certificate xc) throws SSLException {
            }

            @Override
            public void verify(String string, String[] strings, String[] strings1) throws SSLException {
            }

            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        };

        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, verifier);
        ClientConnectionManager ccm = base.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
        DefaultHttpClient httpclient = new DefaultHttpClient(ccm, base.getParams());
        httpclient.setRedirectStrategy(new LaxRedirectStrategy());
        
        try {
            HttpPost httpost;
            HttpResponse response;
            HttpEntity entity;
            List<Cookie> cookies;
            BufferedReader rd;
            String line;
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();            
                                    
            //log in
            httpost = new HttpPost("myloginurl");

            nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("login", "Log In"));
            nvps.add(new BasicNameValuePair("os_username", "foo"));
            nvps.add(new BasicNameValuePair("os_password", "foobar"));
            nvps.add(new BasicNameValuePair("os_cookie", "true"));
            nvps.add(new BasicNameValuePair("os_destination", ""));
                        
            httpost.setEntity(new UrlEncodedFormEntity(nvps));
            response = httpclient.execute(httpost);
            System.out.println(response.toString());
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            } 

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
    
}
