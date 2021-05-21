package com.ssafy.runnershi.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.Session;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenViduCustom {
  private static final Logger log = LoggerFactory.getLogger(OpenVidu.class);
  private String secret;
  protected String hostname;
  protected HttpClient httpClient;
  protected Map<String, Session> activeSessions = new ConcurrentHashMap();
  protected static final String API_PATH = "openvidu/api";
  protected static final String API_SESSIONS = "openvidu/api/sessions";
  protected static final String API_TOKENS = "openvidu/api/tokens";
  protected static final String API_RECORDINGS = "openvidu/api/recordings";
  protected static final String API_RECORDINGS_START = "openvidu/api/recordings/start";
  protected static final String API_RECORDINGS_STOP = "openvidu/api/recordings/stop";

  public OpenViduCustom(String hostname, String secret) {
    this.hostname = hostname;
    if (!this.hostname.endsWith("/")) {
      this.hostname = this.hostname + "/";
    }

    this.secret = secret;
    TrustStrategy trustStrategy = new TrustStrategy() {
      public boolean isTrusted(
          X509Certificate[] chain, String authType) throws CertificateException {
        return true;
      }
    };
    CredentialsProvider provider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("OPENVIDUAPP", this.secret);
    provider.setCredentials(AuthScope.ANY, credentials);

    SSLContext sslContext;
    try {
      sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, trustStrategy).build();
    } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException var8) {
      throw new RuntimeException(var8);
    }

    Builder requestBuilder = RequestConfig.custom();
    requestBuilder = requestBuilder.setConnectTimeout(30000);
    requestBuilder = requestBuilder.setConnectionRequestTimeout(30000);
    this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build()).setConnectionTimeToLive(30L, TimeUnit.SECONDS).setSSLHostnameVerifier(
        NoopHostnameVerifier.INSTANCE).setSSLContext(sslContext).setDefaultCredentialsProvider(provider).build();
  }

  public List<String> getSessionList() {
    HttpGet request = new HttpGet(this.hostname + "openvidu/api/sessions");

    List<String> list = new ArrayList<>();
    HttpResponse response;
    try {
      response = this.httpClient.execute(request);
    } catch (IOException var11) {
      return null;
    }


    try {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        return null;
      }

      JsonObject json = this.httpResponseToJson(response);

      int a = Integer.parseInt(json.get("numberOfElements").toString());
      System.out.println("총 방의 개수: " + a);

      JsonArray array = json.get("content").getAsJsonArray();
      System.out.println(array);

      /*List<Recording> recordings = new ArrayList();
      JsonObject json = this.httpResponseToJson(response);
      JsonArray array = json.get("items").getAsJsonArray();
      array.forEach((item) -> {
        recordings.add(new Recording(item.getAsJsonObject()));
      });*/

    } finally {
      EntityUtils.consumeQuietly(response.getEntity());
    }

    return list;
  }

  private JsonObject httpResponseToJson(HttpResponse response) {
    try {
      JsonObject json = (JsonObject)(new Gson()).fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
      return json;
    } catch (ParseException | IOException | JsonSyntaxException var3) {
      return null;
    }
  }

}
