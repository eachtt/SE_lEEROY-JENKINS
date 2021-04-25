package com.example.runnertest.accountmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class AccountConnector {
    private static URL LOGIN_URL;
    private static URL LOGOUT_URL;
    private static URL REGISTER_URL;
    private static URL IS_LOGIN_URL;

    static {
        try {
            // for_test : 123456
            String LOGIN_PATH = "http://47.100.45.27:8080/account/login";
            LOGIN_URL = new URL(LOGIN_PATH);
            String LOGOUT_PATH = "http://47.100.45.27:8080/account/logout";
            LOGOUT_URL = new URL(LOGOUT_PATH);
            String REGISTER_PATH = "http://47.100.45.27:8080/account/register";
            REGISTER_URL = new URL(REGISTER_PATH);
            String IS_LOGIN_PATH = "http:/47.100.45.27:8080/account/islogin";
            IS_LOGIN_URL = new URL(IS_LOGIN_PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String sendPostMessage(URL url, Map<String, String> params, String encode) {
        StringBuilder stringBuilder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    stringBuilder.append(entry.getKey()).append("=")
                                 .append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                byte[] data = stringBuilder.toString().getBytes();
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length",
                        String.valueOf(data.length));
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(data, 0, data.length);
                outputStream.close();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), encode));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    // return token of "" if it failed
    public static String login(String username, String password) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("password", getSHA256(password));
        String res = sendPostMessage(LOGIN_URL, params, "UTF-8");
        String r[] = res.split(",");
        if (r[1].equals("\"status\":-1}")) {
            return "";
        } else {
            return r[0].split("\"")[3];
        }
    }

    public static String register(String username, String nickname, String password) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", getSHA256(password));
        String res = sendPostMessage(REGISTER_URL, params, "UTF-8");

        // if register is success, login automatically
        if (res.equals("")) return "";
        return login(username, password);
    }

    public static String logout(String username, String token) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("token", token);
        String res = sendPostMessage(LOGOUT_URL, params, "UTF-8");

        return "";
    }

    public static boolean isLogin(String username, String token) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("username", username);
        params.put("token", token);
        String res = sendPostMessage(IS_LOGIN_URL, params, "UTF-8");
        String r[] = res.split(",");
        String code = r[0].split(":")[1];

        return !code.equals("-1");
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String temp;
            byte []bytes = messageDigest.digest();
            for (byte aByte : bytes) {
                temp = Integer.toHexString(aByte & 0xFF);
                if (temp.length() == 1) stringBuilder.append("0");
                stringBuilder.append(temp);
            }
            encodestr = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodestr;
    }


}
