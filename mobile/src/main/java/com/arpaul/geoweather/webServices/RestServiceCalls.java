package com.arpaul.geoweather.webServices;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Aritra on 01-08-2016.
 */
public class RestServiceCalls {

    private String url = "";
    private String params = "";
    private WEBSERVICE_TYPE type;
    private final int TIMEOUT = 10000;
    private WebServiceResponse responseDo;

    public RestServiceCalls(String url, String params, WEBSERVICE_TYPE type){
        this.url                = url;
        this.params             = params;
        this.type               = type;

        responseDo = new WebServiceResponse();

        switch (type){
            case GET:
                getJSON(url);
                break;

            case POST:
                postJSON(url, params);
                break;
        }
    }

    public WebServiceResponse getData(){

        return responseDo;
    }

    /**
     * Get data in JSON format.
     * @param URL
     */
    private void getJSON(String URL) {
        HttpURLConnection httpClient = null;
        StringBuilder sb = null;
        try {
            java.net.URL url = new URL(URL);
            httpClient = (HttpURLConnection) url.openConnection();
            httpClient.setRequestMethod(WebServiceConstant.GET);
            httpClient.setRequestProperty("Content-length","0");
            httpClient.setUseCaches(false);
            httpClient.setAllowUserInteraction(false);
            httpClient.setConnectTimeout(TIMEOUT);
            httpClient.setReadTimeout(TIMEOUT);

            if(!TextUtils.isEmpty(params)){
                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(httpClient.getOutputStream(), "UTF-8"));
                writer.write(params);
                writer.flush();
                writer.close();
            }

            httpClient.connect();

            int status = httpClient.getResponseCode();
            InputStream stream;
            switch (status) {
                case WebServiceConstant.STATUS_SUCCESS :
                    stream = httpClient.getInputStream();
                    responseDo.setResponseCode(WebServiceResponse.ResponseType.SUCCESS);
                    break;

                case WebServiceConstant.STATUS_FAILED:
                default:
                    stream = httpClient.getErrorStream();
                    responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
                    break;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

        } catch(MalformedURLException ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } catch(IOException ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } finally {
            if(httpClient != null) {
                httpClient.disconnect();
            }

            if(sb != null/* && responseDo.getResponseCode() == WebServiceResponse.ResponseType.SUCCESS*/)
                responseDo.setResponseMessage(sb.toString());

            return;
        }
    }

    /**
     * Post data in JSON format.
     * @param URL
     * @param params
     */
    private void postJSON(String URL, String params) {
        HttpURLConnection httpClient = null;
        StringBuilder sb = null;
        BufferedReader reader = null;
        try {
            java.net.URL url = new URL(URL);
            httpClient = (HttpURLConnection) url.openConnection();
            httpClient.setDoInput(true);
            httpClient.setDoOutput(true);
            httpClient.setRequestMethod(WebServiceConstant.POST);
            httpClient.setRequestProperty("Content-length", "application/json");
            httpClient.setRequestProperty("Accept", "application/json");
            httpClient.setUseCaches(false);
            httpClient.setAllowUserInteraction(false);
            httpClient.setConnectTimeout(TIMEOUT);
            httpClient.setReadTimeout(TIMEOUT);

            if(!TextUtils.isEmpty(params)){
                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(httpClient.getOutputStream(), "UTF-8"));
                writer.write(params);
                writer.flush();
                writer.close();
            }

            httpClient.connect();

            int status = httpClient.getResponseCode();
            InputStream stream;
            switch (status) {
                case WebServiceConstant.STATUS_SUCCESS :
                case WebServiceConstant.STATUS_CREATED :
                case WebServiceConstant.STATUS_ACCEPTED :
                case WebServiceConstant.STATUS_NO_CONTENT :
                    stream = httpClient.getInputStream();
                    responseDo.setResponseCode(WebServiceResponse.ResponseType.SUCCESS);
                    break;

                case WebServiceConstant.STATUS_FAILED:
                default:
                    stream = httpClient.getErrorStream();
                    responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
                    break;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

        } catch(MalformedURLException ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } catch(IOException ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } catch(Exception ex) {
            ex.printStackTrace();
            responseDo.setResponseCode(WebServiceResponse.ResponseType.FAILURE);
            responseDo.setResponseMessage(ex.toString());
        } finally {
            if(httpClient != null) {
                httpClient.disconnect();
            }

            if(sb != null/* && responseDo.getResponseCode() == WebServiceResponse.ResponseType.SUCCESS*/)
                responseDo.setResponseMessage(sb.toString());

            return;
        }
    }
}
