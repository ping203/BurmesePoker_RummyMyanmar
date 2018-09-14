package net.myanmar.rummy.vo;

public class BotCreateTable {
	public BotCreateTable() {
		
	}
	
	public BotCreateTable(int IDCase, int on, int markMin, int markMax, int tableMin, int tableMax, int timeCheck, int tableToAddMin, int tableToAddMax) {
		this.IDCase = IDCase;
		this.on = on;
		this.markMin = markMin;
		this.markMax = markMax;
		this.tableMin = tableMin;
		this.tableMax = tableMax;
		this.timeCheck = timeCheck;
		this.tableToAddMin = tableToAddMin;
		this.tableToAddMax = tableToAddMax;
	}
	
	private int IDCase;
	private int on;
	private int markMin;
	private int markMax;
	private int tableMin;
	private int tableMax;
	private int timeCheck;
	private int tableToAddMin;
	private int tableToAddMax;
	
	public int getIDCase() {
		return IDCase;
	}
	
	public int getOn() {
		return on;
	}
	
	public int getMarkMin() {
		return markMin;
	}
	
	public int getMarkMax() {
		return markMax;
	}
	
	public int getTableMin() {
		return tableMin;
	}
	
	public int getTableMax() {
		return tableMax;
	}
	
	public int getTimeCheck() {
		return timeCheck;
	}
	
	public int getTableToAddMin() {
		return tableToAddMin;
	}
	
	public int getTableToAddMax() {
		return tableToAddMax;
	}
}
