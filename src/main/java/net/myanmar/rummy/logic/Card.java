package net.myanmar.rummy.logic;

public class Card {

    private int S;
    private int N;
    private int I;
    
    public Card() {
        this.S = 0;
        this.N = 0;
        this.I = 0;
    }

    public Card(int s, int n, int i) {
        this.S = s;
        this.N = n;
        this.I = i;
    }

    public Card(int i) {
        this.I = i;
        if (i == 60 || i == 61) {
            this.S = this.N = i;
        } else {
            this.S = (i - 1) / 13 + 1;
            this.N = (i - 1) % 13 + 2;
        }
    }

    public boolean Compare(Card c1) {
        if (this.N > c1.getN()) {
            return true;
        } else if (this.N == c1.getN()) {
            return this.S > c1.getS();
        } else {
            return false;
        }
    }
    
    public int getI() {
        return I;
    }

    public void setI(int i) {
        I = i;
    }

    public int getS() {
        return S;
    }

    public void setS(int s) {
        S = s;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int calc() {
        
        if(N < 10) return N;
        if(N == 14) return 1;
        return 10;

    }

    public boolean isJocker() {
        return this.N == 60 || this.N == 61;
    }

    @Override
    public String toString() {
        String number;
        String suit = "";
        switch (S) {
            case 1:
                suit = "♠";
                break;
            case 2:
                suit = "♣";
                break;
            case 3:
                suit = "♦";
                break;
            case 4:
                suit = "♥";
                break;
            default:
                break;
        }
        
        switch (N) {
            case 14:
                number = "A";
                break;
            case 13:
                number = "K";
                break;
            case 12:
                number = "Q";
                break;
            case 11:
                number = "J";
                break;
            case 60:
                number = "JKB";
                break;
            case 61:
                number = "JKR";
                break;
            default:
                number = Integer.toString(N);
                break;
        }
        
        return number+suit;
    }
}
