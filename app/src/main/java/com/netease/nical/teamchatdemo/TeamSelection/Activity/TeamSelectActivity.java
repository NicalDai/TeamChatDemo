package com.netease.nical.teamchatdemo.TeamSelection.Activity;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netease.nical.teamchatdemo.Login.Activity.MainActivity;
import com.netease.nical.teamchatdemo.R;
import com.netease.nical.teamchatdemo.TeamMemberSelection.Activity.MemberSelectActivity;
import com.netease.nical.teamchatdemo.TeamSelection.TeamListAdapter;
import com.netease.nim.avchatkit.common.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TeamSelectActivity extends AppCompatActivity {

    private RecyclerView teamlistview;
    private TeamListAdapter adapter;
    private List<Team> teams = new ArrayList<>();
    private static String currentLoginAccount;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        currentLoginAccount = intent.getStringExtra("currentLoginAccount");
        // 初始化视图
        initView();
        // 初始化RecyclerView
        initRecyclerView();
        //设置Item点击事件
        setItemOnClickListener();

        //注册数据同步的观察者
        registerSyncState(true);
        //Get Team List
//        getTeam();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TeamSelectActivity", "Logout:  登出");
                NIMClient.getService(AuthService.class).logout();
                Toast.makeText(TeamSelectActivity.this, "正在登出......", Toast.LENGTH_SHORT).show();
                SystemClock.sleep(1000);
                Intent intent = new Intent(TeamSelectActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerSyncState(Boolean register){
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(new Observer<LoginSyncStatus>() {
            @Override
            public void onEvent(LoginSyncStatus status) {
                if (status == LoginSyncStatus.BEGIN_SYNC) {
                    LogUtil.i("observeLoginSyncDataStatus", "login sync data begin");
                } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
                    LogUtil.i("observeLoginSyncDataStatus", "login sync data completed");
                    Toast.makeText(TeamSelectActivity.this, "数据同步成功", Toast.LENGTH_SHORT).show();
                    getTeam();
                }
            }
        }, register);
    }


    /**
     * 获取当前登陆的账号
     */
    public static String getCurrentLoginAccount() {
        return currentLoginAccount;
    }

    /**
     * 初始化视图
     */
    private void initView(){
        teamlistview = (RecyclerView) findViewById(R.id.team_list_view);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new TeamListAdapter(teams);
        teamlistview.setAdapter(adapter);
        teamlistview.setLayoutManager(linearLayoutManager);
        teamlistview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        logout = (Button) findViewById(R.id.logout);
    }


    /**
     * SDK获取本地群组数据，并插入到UI
     */
    private void getTeam(){
        List<Team> localteams = NIMClient.getService(TeamService.class).queryTeamListBlock();
        if (!(localteams == null)){
            for (int i = 0;i < localteams.size();i++){
                teams.add(localteams.get(i));
//                adapter.notifyItemInserted(teams.size()-1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Item点击事件
     */
    private void setItemOnClickListener(){
        adapter.setOnItemClickListener(new TeamListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String tid = teams.get(position).getId();
                Intent intent = new Intent(TeamSelectActivity.this, MemberSelectActivity.class);
                intent.putExtra("tid",tid);
                intent.putExtra("tname",teams.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    /**
     * Toast展示一段时间的方法
     * @param toast
     * @param cnt
     */
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }


}
