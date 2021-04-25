package com.example.runnertest.accountmanager;

import android.accounts.Account;

public class AccountUnit {

    public AccountUnit(String username, String token) {
    }

    public String login(String username, String password) {
        String token = AccountConnector.login(username, password);
        return token;
    }

    public String register(String username, String nickname, String password) {
        String token = AccountConnector.register(username, nickname, password);
        return token;
    }

    public boolean isLogin(String username, String token) {
        return AccountConnector.isLogin(username, token);
    }

    public void logout(String username, String token) {
        AccountConnector.logout(username, token);
    }
    
}
