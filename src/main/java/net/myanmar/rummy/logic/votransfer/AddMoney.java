package net.myanmar.rummy.logic.votransfer;

public class AddMoney {

    private String evt;
    private String N;
    private Long M;
    private int T;
    
    public AddMoney(String e, String n, Long m, int t) {
        this.evt = e;
        this.N = n;
        this.M = m;
        this.T = t;
    }

    public int getT() {
        return T;
    }

    public void setT(int t) {
        T = t;
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

    public Long getM() {
        return M;
    }

    public void setM(Long m) {
        M = m;
    }

}
