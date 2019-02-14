package com.netease.nical.teamchatdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.netease.nical.teamchatdemo.Login.Activity.MainActivity;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.config.AVChatOptions;
import com.netease.nim.avchatkit.model.ITeamDataProvider;
import com.netease.nim.avchatkit.model.IUserInfoProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

public class NIMApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this,null,null);
        if (NIMUtil.isMainProcess(this)){
            initAVChatKit();
        }
    }

    private void initAVChatKit(){



        AVChatOptions avChatOptions = new AVChatOptions(){
            @Override
            public void logout(Context context) {
                NIMClient.getService(AuthService.class).logout();
                SystemClock.sleep(1000);
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
            }
        };

        AVChatKit.init(avChatOptions);

        // 设置用户相关资料提供者
        AVChatKit.setUserInfoProvider(new IUserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                UserInfo userInfo =  NIMClient.getService(UserService.class).getUserInfo(account);
                if (userInfo == null) {
                    //如果本地获取不到这个人的用户资料
                }
                return userInfo;
            }

            @Override
            public String getUserDisplayName(String account) {
                UserInfo userInfo =  NIMClient.getService(UserService.class).getUserInfo(account);
                if (userInfo == null) {
                    return "未知用户";
                }else {
                    return userInfo.getName();
                }
            }
        });

        // 设置群组数据提供者
        AVChatKit.setTeamDataProvider(new ITeamDataProvider() {
            @Override
            public String getDisplayNameWithoutMe(String teamId, String account) {

                TeamMember teamMember =  NIMClient.getService(TeamService.class).queryTeamMemberBlock(teamId,account);
                if(teamMember == null) {
                    return null;
                } else {
                    if(teamMember.getTeamNick().isEmpty()){
                        return null;
                    } else {
                        return teamMember.getTeamNick();
                    }
                }
            }

            @Override
            public String getTeamMemberDisplayName(String teamId, String account) {
                TeamMember teamMember =  NIMClient.getService(TeamService.class).queryTeamMemberBlock(teamId,account);
                if(teamMember == null) {
                    return null;
                } else {
                    if(teamMember.getTeamNick().isEmpty()){
                        return null;
                    } else {
                        return teamMember.getTeamNick();
                    }
                }
            }
        });
    }
}
