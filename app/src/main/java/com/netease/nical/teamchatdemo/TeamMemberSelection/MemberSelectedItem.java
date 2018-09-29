package com.netease.nical.teamchatdemo.TeamMemberSelection;

public class MemberSelectedItem {

    private String memberNick;
    private String memberAccid;

    public String getMemberAccid() {
        return memberAccid;
    }

    public String getMemberNick() {
        return memberNick;
    }

    public void setMemberAccid(String memberAccid) {
        this.memberAccid = memberAccid;
    }

    public void setMemberNick(String memberNick) {
        this.memberNick = memberNick;
    }

    public MemberSelectedItem(String memberNick,String memberAccid){
        this.memberAccid = memberAccid;
        this.memberNick = memberNick;
    }
}
