package net.myanmar.rummy.logic;

public class PlayCard {

    public PlayCard(String n, String e, int c) {
        this.N = n;
        this.C = c;
        this.evt = e;
    }

    public PlayCard(String n, String e, int c, String nn) {
        this.N = n;
        this.C = c;
        this.evt = e;
        this.NN = nn;
    }

    private String N;
    private int C;
    private String evt;
    private String NN;

    public int getC() {
        return C;
    }

    public void setC(int c) {
        C = c;
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

    public void setNS(String nn) {
        NN = nn;
    }
}
