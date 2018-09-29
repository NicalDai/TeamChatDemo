package com.netease.nical.teamchatdemo.TeamSelection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nical.teamchatdemo.R;
import com.netease.nimlib.sdk.team.model.Team;
import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder>{
    private List<Team> teams;
    OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout teamListItem;  //群组Item对象
        TextView tname;             //群组名称

        public ViewHolder(View view){
            super(view);
            teamListItem = (LinearLayout) view.findViewById(R.id.teamlistitem);
            tname = (TextView) view.findViewById(R.id.teamname);
        }
    }

    public TeamListAdapter(List<Team> teams){
        this.teams = teams;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item,parent,false);
        //Inflate 对应的View
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Team team = teams.get(position);
        if (team.getName() != null){
            holder.tname.setText(team.getName() + " ("+team.getId() + ")");
        }else {
            holder.tname.setText(team.getId());
        }

        if(onItemClickListener != null){
            holder.teamListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                }
            });
            holder.teamListItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    public int getItemCount(){
        return teams.size();
    }

    public interface OnItemClickListener {
        void onClick( int position);
        void onLongClick( int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.onItemClickListener = onItemClickListener;
    }
}
