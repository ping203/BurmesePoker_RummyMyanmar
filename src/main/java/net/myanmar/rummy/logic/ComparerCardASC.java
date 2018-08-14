package net.myanmar.rummy.logic;

import java.util.Comparator;


public class ComparerCardASC implements Comparator<Card>{

	@Override
	public int compare(Card arg0, Card arg1) {
		// TODO Auto-generated method stub
		if(arg0.getN() > arg1.getN()) return 1;
		else if(arg0.getN() == arg1.getN()) return 0;
		else return -1;
	}

}
