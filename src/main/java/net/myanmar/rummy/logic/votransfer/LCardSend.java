package net.myanmar.rummy.logic.votransfer;

public class LCardSend {

    private String evt;
    private int[] arr;
    private String nextTurn;
    private int deckCount;

     public LCardSend(String evt, int[] arr, String username, int deckCount) {
        this.evt = evt;
        this.arr = arr;
        this.nextTurn = username;
        this.deckCount = deckCount;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }


    public int[] getArr() {
        return arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public void setNextTurn(String turn) {
        this.nextTurn = turn;
    }

    public String getNextTurn() {
        return nextTurn;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    

}
