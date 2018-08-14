package net.myanmar.rummy.logic;

public class TipSend {

    public TipSend(String evt, int agtip, String N) {
        this.N = N;
        this.evt = evt;
        this.AGTip = agtip;
    }

    private final String evt;
    private final String N;
    private final int AGTip;

}
