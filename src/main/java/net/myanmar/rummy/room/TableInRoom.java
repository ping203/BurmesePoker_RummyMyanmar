/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.room;

public class TableInRoom {
    private int id;
    private long mark;
    private long minChip;
    private short capacity;
    private short seated;

    public TableInRoom(int id, long mark, long minchip, short seated, short capacity){
        this.id = id;
        this.mark = mark;
        this.minChip = minchip;
        this.seated = seated;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getMark() {
        return mark;
    }
    public void setMark(long mark) {
        this.mark = mark;
    }
    public long getMinChip() {
        return minChip;
    }
    public void setMinChip(long minChip) {
        this.minChip = minChip;
    }
    public short getCapacity() {
        return capacity;
    }
    public void setCapacity(short capacity) {
        this.capacity = capacity;
    }

    public short getSeated() {
        return seated;
    }

    public void setSeated(short seated) {
        this.seated = seated;
    }
}
