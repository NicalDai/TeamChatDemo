package com.netease.nical.teamchatdemo.TeamMemberSelection;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nical.teamchatdemo.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.ViewHolder>{

    private List<TeamMember> members;
    private List<Boolean> isSelected;
    private List<TeamMember> selectedMembers;
    private OnItemClickListener onItemClickListener;
    private Context context; //为了在对应的Activity上toast一个提示。

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout memberItem;
        TextView memberName;
        OnItemClickListener onItemClickListener;

        public ViewHolder(View view){
            super(view);
            memberItem = (LinearLayout) view.findViewById(R.id.member_item);
            memberName = (TextView) view.findViewById(R.id.membername);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null){
//                onItemClickListener.onClick(getAdapterPosition(),v);
            }
        }
    }

    public TeamMemberAdapter(List<TeamMember> teamMembers){
        members = teamMembers;
        isSelected = new ArrayList<>();
        selectedMembers = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false);
        //先将标志位全部置为false
        for (int i = 0;i < members.size();i++){
            isSelected.add(false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TeamMember teamMember = members.get(position);

        if(isSelected.get(position)){
            holder.memberItem.setBackgroundColor(Color.parseColor("#00a0e9"));
        }else{
            holder.memberItem.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        if(teamMember.getTeamNick() == null || teamMember.getTeamNick().equals("")){
            NimUserInfo userInfo =  NIMClient.getService(UserService.class).getUserInfo(teamMember.getAccount());
            if(userInfo == null){
                List<String> accounts = new ArrayList<>();
                accounts.add(teamMember.getAccount());
                NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override public void onSuccess(List<NimUserInfo> nimUserInfos) {
                        holder.memberName.setText(nimUserInfos.get(0).getName() + " (" + nimUserInfos.get(0).getAccount() + ") ");
                        Log.d("获取云端群成员资料", "onSuccess: "); }

                    @Override public void onFailed(int i) {
                        Log.d("获取云端群成员资料", "onFailed: ");
                    }

                    @Override public void onException(Throwable throwable) {
                        Log.d("获取云端群成员资料", "onException: ");
                    }
                });
            } else {
                holder.memberName.setText(userInfo.getName() + " (" + userInfo.getAccount() + ")");
            }
        }else {
            holder.memberName.setText(teamMember.getTeamNick() + " (" + teamMember.getAccount()+")");
        }



        if(onItemClickListener != null){
            holder.memberItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelected.get(position)){    //设置对应position的UI展示变化，以及群组队列调整
                            isSelected.set(position,false);
                            selectedMembers.remove(members.get(position));
                            notifyDataSetChanged();
                            MemberSelectedItem memberSelectedItem = new MemberSelectedItem(teamMember.getTeamNick(),teamMember.getAccount());
                            onItemClickListener.onClick(position,memberSelectedItem,false);
                    }else {
                        if(selectedMembers.size() < 9 ) {
                            isSelected.set(position, true);
                            notifyDataSetChanged();
                            selectedMembers.add(teamMember);
                            MemberSelectedItem memberSelectedItem = new MemberSelectedItem(teamMember.getTeamNick(), teamMember.getAccount());
                            onItemClickListener.onClick(position, memberSelectedItem, true);
                        } else {
                            Toast.makeText(context, "Demo最多支持9人音视频聊天，sdk可以支持更多，上限200人。", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            holder.memberItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    public void setContext(Context context){
        this.context = context;
    }

    public int getItemCount(){
        return members.size();
    }

    public interface OnItemClickListener {
        void onClick(int position,MemberSelectedItem memberSelectedItem,Boolean flag);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
