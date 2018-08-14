package net.myanmar.rummy.logic;

public class BlindTrans {

    public BlindTrans(String evt, String n, String nn, long totalAg, long agLose, int diem) {
        this.evt = evt;
        this.N = n;
        this.NN = nn;
        this.TotalAG = totalAg;
        this.agLose = agLose;
        this.diem = diem;
    }

    public BlindTrans() {
        N = "";
        NN = "";
        AG = 0l;
        TotalAG = 0l;
        AGADD = 0l;
        this.agLose = 0;
        this.diem = 0;
    }
    private String N;
    private String NN;
    private Long AG;
    private Long TotalAG;
    private Long AGADD;
    private String evt;
    private long agLose;
    private int diem;

    public BlindTrans(String evt, String username, String nextPlayer) {
        this.evt = evt;
        this.N = username;
        this.NN = nextPlayer;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getNN() {
        return NN;
    }

    public void setNN(String nN) {
        NN = nN;
    }

    public Long getAG() {
        return AG;
    }

    public void setAG(Long aG) {
        AG = aG;
    }

    public Long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(Long totalAG) {
        TotalAG = totalAG;
    }

    public Long getAGADD() {
        return AGADD;
    }

    public void setAGADD(Long aGADD) {
        AGADD = aGADD;
    }

    public long getAgLose() {
        return agLose;
    }

    public void setAgLose(long ag) {
        this.agLose = ag;
    }

    public int getDiem() {
        return diem;
    }

    public void setDiem(int diem) {
        this.diem = diem;
    }

}
