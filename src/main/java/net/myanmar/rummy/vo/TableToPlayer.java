/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.vo;

/**
 *
 * @author UserXP
 */
public class TableToPlayer {

    private int TableId;
    private int Pid;

    /**
     * Get the value of Pid
     *
     * @return the value of Pid
     */
    public int getPid() {
        return Pid;
    }

    /**
     * Set the value of Pid
     *
     * @param Pid new value of Pid
     */
    public void setPid(int Pid) {
        this.Pid = Pid;
    }

    /**
     * Get the value of TableId
     *
     * @return the value of TableId
     */
    public int getTableId() {
        return TableId;
    }

    /**
     * Set the value of TableId
     *
     * @param TableId new value of TableId
     */
    public void setTableId(int TableId) {
        this.TableId = TableId;
    }
}
