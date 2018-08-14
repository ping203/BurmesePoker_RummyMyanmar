package net.myanmar.rummy.vo;

public class TableIdle {

	public TableIdle(int id,int m,int v,int roomid){
		Tid = id;
		Mark = m;
		vip = v ;
//		roomid = roomid ;
	}
	private int Tid;
	private int Mark;
	private int vip ;
//	private int roomid ;
//	
//	public int getRoomid() {
//		return roomid;
//	}
//	public void setRoomid(int roomid) {
//		this.roomid = roomid;
//	}
	public int getVip() {
		return vip;
	}
	public void setVip(int vip) {
		this.vip = vip;
	}
	public int getTid() {
		return Tid;
	}
	public void setTid(int tid) {
		Tid = tid;
	}
	public int getMark() {
		return Mark;
	}
	public void setMark(int mark) {
		Mark = mark;
	}
	
	
}
