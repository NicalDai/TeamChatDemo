package com.netease.nical.teamchatdemo.TeamMemberSelection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nical.teamchatdemo.R;

import java.util.List;

public class TeamSelectedAdapter extends RecyclerView.Adapter<TeamSelectedAdapter.ViewHolder>{

    private List<MemberSelectedItem> selectedMembers;
    private TeamMemberAdapter.OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout memberselectedview;
        TextView selectedMember;

        public ViewHolder(View view){
            super(view);
            memberselectedview = (LinearLayout) view.findViewById(R.id.member_selected_item);
            selectedMember = (TextView) view.findViewById(R.id.member_selected);
        }
    }

    public TeamSelectedAdapter(List<MemberSelectedItem> selectedMembers){
        this.selectedMembers =  selectedMembers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_selected_item,parent,false);
            //先将标志位全部置为false
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberSelectedItem selectedItem = selectedMembers.get(position);
        holder.selectedMember.setText(selectedItem.getMemberAccid());
    }

    @Override
    public int getItemCount() {
        return selectedMembers.size();
    }
}
