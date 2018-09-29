package com.netease.nical.teamchatdemo.InfoProvider;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

public class TeamUserInfoProvider {

    public void getAccountNick(String accid){
        NimUserInfo userInfo =  NIMClient.getService(UserService.class).getUserInfo(accid);
        if(userInfo == null){
            List<String> accounts = new ArrayList<>();
            accounts.add(accid);
            NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> nimUserInfos) {

                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        }
    }
}
