package com.license.manager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientDemo {

    static String POST_URL = "http://android.api.techsofts.in/register.php";
    static String GET_URL = "http://android.api.techsofts.in/get.php";

    public static void main(String[] args) throws IOException {

        uploadImageToServer();

    }
        private static void instaCheck () throws IOException {

            String url = "https://www.instagram.com/p/CRIpFwTLJvW/?__a=1";

            HttpGet httpPost = new HttpGet(url);

            HttpClient httpClient = HttpClients.createDefault();

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();
            String content = EntityUtils.toString(respEntity);

            //  System.out.println(content);

            JSONObject jb = new JSONObject(content);

            System.out.println(jb.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url"));
        }

        private static void getMethod () {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(GET_URL);

      /*  List<User> userList = new ArrayList<>();

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jo = (JSONObject) jsonArray.get(i);

                    int id = jo.getInt("id");
                    String name = jo.getString("name");
                    String email = jo.getString("email");
                    String password = jo.getString("password");
                    userList.add(new User(id,name,email,password));
                }


              for(User user :userList){

                  System.out.println("id : "+user.getId());
                  System.out.println("name : "+user.getName());
                  System.out.println("email : "+user.getEmail());
                  System.out.println("password : "+user.getPassword());

                  System.out.println("----------------------------");
              }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }*/

        }

        private static void postMethod () {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(POST_URL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", "pk3232323ek"));
            params.add(new BasicNameValuePair("email", "em23232@gmail.com"));
            params.add(new BasicNameValuePair("password", "pass17"));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity respEntity = response.getEntity();

                if (respEntity != null) {
                    String content = EntityUtils.toString(respEntity);

                    if (!"".equals(content)) {
                        JSONObject jsonObject = new JSONObject(content);
                        System.out.println(jsonObject.getString("message"));
                    }


                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }


       static void sendImageToFile() throws IOException {

        //   uploadfile

        String url = "http://localhost/LicenseManagerAPI/backup.php";
        File file = new File("E:\\My Mobile\\Xender\\image\\my_pic.jpg");

           HttpClient httpClient = HttpClients.createDefault();
           HttpPost httpPost = new HttpPost(url);
           List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("choosefile", file.getAbsolutePath()));
           try {
               httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }


           try {
               HttpResponse response = httpClient.execute(httpPost);
               HttpEntity respEntity = response.getEntity();

               if (respEntity != null) {
                   String content = EntityUtils.toString(respEntity);

                   System.out.println(content);

                  /* if (!"".equals(content)) {
                       JSONObject jsonObject = new JSONObject(content);
                       System.out.println(jsonObject.getString("message"));
                   }*/


               }
           } catch (IOException e) {
               System.out.println(e.getMessage());
               e.printStackTrace();
           }


        }
    public static void uploadImageToServer() throws IOException {

        String url = "http://localhost/LicenseManagerAPI/backup.php";
        File file = new File("E:\\My Mobile\\Xender\\image\\my_pic.jpg");
        String app_id = "12334";

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(file);

        builder.addPart("file", fileBody);
        builder.addTextBody("app_id",app_id);
        builder.addTextBody("p","123");
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity resEntity = response.getEntity();

        if (resEntity != null) {

            String content = EntityUtils.toString(resEntity);

           JSONObject jo = new JSONObject(content);

           int statusCode = jo.getInt("statusCode");
           String msg = jo.getString("message");

           if (statusCode == 200){
               System.out.println(msg);

           }else {
               System.out.println(msg);
           }
        }

    }


    }


