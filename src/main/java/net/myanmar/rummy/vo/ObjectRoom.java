/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.vo;

import java.io.Serializable;

/**
 *
 * @author UserXP
 */
public class ObjectRoom implements Serializable {

    private int Id;
    private String Name;
    private int MaxTable;
    private int CurPlay;
    private int MaxPlay;
    private int CurTable;
    private int MinAG;
    private int MaxAG;
    private int Mark;


    
    public int getMark() {
        return Mark;
    }

    public void setMark(int mark) {
        Mark = mark;
    }

    public int getMinAG() {
        return MinAG;
    }

    public void setMinAG(int minAG) {
        MinAG = minAG;
    }

    public int getMaxAG() {
        return MaxAG;
    }

    public void setMaxAG(int maxAG) {
        MaxAG = maxAG;
    }

    /**
     * Get the value of CurTable
     *
     * @return the value of CurTable
     */
    public int getCurTable() {
        return CurTable;
    }

    /**
     * Set the value of CurTable
     *
     * @param CurTable new value of CurTable
     */
    public void setCurTable(int CurTable) {
        this.CurTable = CurTable;
    }

    /**
     * Get the value of MaxPlay
     *
     * @return the value of MaxPlay
     */
    public int getMaxPlay() {
        return MaxPlay;
    }

    /**
     * Set the value of MaxPlay
     *
     * @param MaxPlay new value of MaxPlay
     */
    public void setMaxPlay(int MaxPlay) {
        this.MaxPlay = MaxPlay;
    }

    /**
     * Get the value of CurPlay
     *
     * @return the value of CurPlay
     */
    public int getCurPlay() {
        return CurPlay;
    }

    /**
     * Set the value of CurPlay
     *
     * @param CurPlay new value of CurPlay
     */
    
    public void setCurPlay(int CurPlay) {
        this.CurPlay = CurPlay;
    }

    /**
     * Get the value of MaxTable
     *
     * @return the value of MaxTable
     */
    public int getMaxTable() {
        return MaxTable;
    }

    /**
     * Set the value of MaxTable
     *
     * @param MaxTable new value of MaxTable
     */
    
    public void setMaxTable(int MaxTable) {
        this.MaxTable = MaxTable;
    }

    /**
     * Get the value of Name
     *
     * @return the value of Name
     */
    public String getName() {
        return Name;
    }

    /**
     * Set the value of Name
     *
     * @param Name new value of Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * Get the value of Id
     *
     * @return the value of Id
     */
    public int getId() {
        return Id;
    }

    /**
     * Set the value of Id
     *
     * @param Id new value of Id
     */
    
    public void setId(int Id) {
        this.Id = Id;
    }

}
