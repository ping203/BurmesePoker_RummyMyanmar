package net.myanmar.rummy.logic;


public class MarkCreateTableTran {

    private int M;
    private int D; //0-Disable, 1-Enable, 2-Default

    public MarkCreateTableTran(int d, int m) {
        this.D = d;
        this.M = m;
    }

    public int getM() {
        return M;
    }

    public void setM(int m) {
        M = m;
    }

    public int getD() {
        return D;
    }

    public void setD(int d) {
        D = d;
    }
}
