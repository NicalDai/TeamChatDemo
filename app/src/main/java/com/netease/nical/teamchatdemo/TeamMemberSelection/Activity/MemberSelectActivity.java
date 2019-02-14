package com.netease.nical.teamchatdemo.TeamMemberSelection.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nical.teamchatdemo.R;
import com.netease.nical.teamchatdemo.TeamMemberSelection.MemberSelectedItem;
import com.netease.nical.teamchatdemo.TeamMemberSelection.TeamMemberAdapter;
import com.netease.nical.teamchatdemo.TeamMemberSelection.TeamSelectedAdapter;
import com.netease.nical.teamchatdemo.TeamSelection.Activity.TeamSelectActivity;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.TeamAVChatProfile;
import com.netease.nim.avchatkit.common.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberSelectActivity extends AppCompatActivity {

    private String tname;
    private String tid;
    private TextView teamShowName;
    private Button startMeetingBtn;
    private Button back;
    private RecyclerView memberselectRcyView;
    private RecyclerView selectResultRcyView;
    private TeamMemberAdapter adapter;
    private TeamSelectedAdapter teamSelectedAdapter;
    private List<TeamMember> members = new ArrayList<>();

    private Boolean isFirstShow = false;

    private List<MemberSelectedItem> selectedItems = new ArrayList<>();

    private Map positionPair = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_select);
        getSupportActionBar().hide();



        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        tname = intent.getStringExtra("tname");

        getTeamMember(tid);

        //初始化View
        initView();
        //初始化RecyclerView
        initRecyclerView();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        startMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<String> accounts = new ArrayList<>();

                if(selectedItems.isEmpty()){
                    Toast.makeText(MemberSelectActivity.this, "请选择需要发起的账号", Toast.LENGTH_SHORT).show();
                } else {

                    for (int i = 0;i < selectedItems.size();i++){
                        accounts.add(selectedItems.get(i).getMemberAccid());
                    }

                    AVChatManager.getInstance().createRoom(tid, null, new AVChatCallback<AVChatChannelInfo>() {
                        @Override
                        public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                            Log.d("create room " + tid," success !");
                            TeamAVChatProfile.sharedInstance().setTeamAVChatting(true);
                            onCreateRoomSuccess(tid,accounts);
                            AVChatKit.outgoingTeamCall(MemberSelectActivity.this,false,tid,tid,accounts,tname);
                        }

                        @Override
                        public void onFailed(int code) {
                            Toast.makeText(MemberSelectActivity.this, "创建多人音视频房间失败，错误码" + code, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            Toast.makeText(MemberSelectActivity.this, "创建多人音视频房间异常，错误码" + exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void initView(){
        teamShowName = (TextView)findViewById(R.id.team_show_name);
        memberselectRcyView = (RecyclerView) findViewById(R.id.team_member_recyclerview);
        selectResultRcyView = (RecyclerView) findViewById(R.id.team_selected_recyclerview);
        startMeetingBtn = (Button) findViewById(R.id.start_meeting);
        back = (Button) findViewById(R.id.back);
        teamShowName.setText(tname);

    }



    /**
     * 初始化两个RecyclerView，一个adapter，一个teamSelectedAdapter
     */
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);

        adapter = new TeamMemberAdapter(members);
        teamSelectedAdapter = new TeamSelectedAdapter(selectedItems);

        adapter.setContext(this);  //这个传过去是为了UI展示

        memberselectRcyView.setAdapter(adapter);
        memberselectRcyView.setLayoutManager(linearLayoutManager);
        selectResultRcyView.setAdapter(teamSelectedAdapter);
        selectResultRcyView.setLayoutManager(linearLayoutManager1);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);   //横向RecyclerView


        //Item的分割线
        memberselectRcyView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        selectResultRcyView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));


        /**
         * 点击Item选中被叫的账号
         */
        adapter.setOnItemClickListener(new TeamMemberAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, MemberSelectedItem memberSelectedItem, Boolean flag) {
                if(flag){
                    if(selectedItems.size() < 9){
                        selectedItems.add(memberSelectedItem);
                        teamSelectedAdapter.notifyItemChanged(selectedItems.size() - 1);
                        positionPair.put(position,selectedItems.size()-1);
                    }else {
                        Toast.makeText(MemberSelectActivity.this, "抱歉，Demo最大支持单个群组9人进行多人音视频通话", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    for(int i = 0;i < selectedItems.size();i++){
                        if(selectedItems.get(i).getMemberAccid().equals(memberSelectedItem.getMemberAccid())){
                            selectedItems.remove(i);
                        }
                    }
                    teamSelectedAdapter.notifyItemRemoved((int)positionPair.get(position));
                }

            }

            @Override
            public void onLongClick(int position) {
                // 长按点击事件
            }
        });
    }

    /**
     * 获取群组成员
     * @param tid
     */
    private void getTeamMember(String tid){
        //获取群组成员列表
        NIMClient.getService(TeamService.class).queryMemberList(tid).setCallback(new RequestCallback<List<TeamMember>>() {
            @Override
            public void onSuccess(List<TeamMember> teamMembers) {
                Log.d("获取群组成员", "onSuccess:  " + teamMembers.size());
                String currentLoginAccount =  TeamSelectActivity.getCurrentLoginAccount();
                for (int i = 0;i < teamMembers.size();i++){
                    if(!teamMembers.get(i).getAccount().equals(currentLoginAccount)){
                        members.add(teamMembers.get(i));
//                        adapter.notifyItemInserted(members.size()-1);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int i) {
                Log.d("获取群组成员失败", "onFailed: ");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.d("获取群组成员异常", "onException: ");
            }
        });
    }


    private void onCreateRoomSuccess(String roomName, List<String> accounts) {

        // 对各个成员发送点对点自定义通知

        String content = TeamAVChatProfile.sharedInstance().buildContent(roomName, tid, accounts, tname);
        CustomNotificationConfig config = new CustomNotificationConfig();
        config.enablePush = true;
        config.enablePushNick = false;
        config.enableUnreadCount = true;

        for (String account : accounts) {
            CustomNotification command = new CustomNotification();
            command.setSessionId(account);
            command.setSessionType(SessionTypeEnum.P2P);
            command.setConfig(config);
            command.setContent(content);

            command.setSendToOnlineUserOnly(false);
            NIMClient.getService(MsgService.class).sendCustomNotification(command);

        }
    }


}
