package net.myanmar.rummy.vo;

import java.util.Comparator;

public class TableIdleSorted implements Comparator<TableIdle>{

	@Override
	public int compare(TableIdle o1, TableIdle o2) {
		// TODO Auto-generated method stub
		if(o1.getMark() > o2.getMark()) return -1;
		else if(o1.getMark()==o2.getMark()) return 0;
		else return 1;
	}
}