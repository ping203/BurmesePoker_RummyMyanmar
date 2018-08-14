package net.myanmar.rummy.vo;

public class MarkCreateTable {

    public MarkCreateTable() {

    }

    public MarkCreateTable(int mark, int ag, int con) {
        this.mark = mark;
        this.ag = ag;
        this.condition = con;
        this.currplay = 0;
    }
    private int mark; //Muc cuoc ban
    private int ag; //So AG toi thieu de tao ban
    private int condition; //1-Tren Web, 2-Tren mobile, 0- Ca Web va Mobile
    private int currplay; //So nguoi choi hien tai

    public int getCurrplay() {
        return currplay;
    }

    public void setCurrplay(int currplay) {
        this.currplay = currplay;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getAg() {
        return ag;
    }

    public void setAg(int ag) {
        this.ag = ag;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
}
