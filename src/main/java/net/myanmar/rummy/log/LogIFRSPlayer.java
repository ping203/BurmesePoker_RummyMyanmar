/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.log;

import java.util.Date;

/**
 *
 * @author UserXP
 */
public class LogIFRSPlayer {

    private int userid;
    private int gold; //Gold con lai
    private int gameid; //Game id
    private int tableid; //TableId
    private int markunit; //M·ª©c c∆∞·ª£c b√†n
    private int goldtransfer; //Gold transfer
    private Date datetransfer; //Date transfer
    private int source;

    public LogIFRSPlayer(int uid, int igold, int igame, int itableid, int imark, int itranfer, Date dtranfer, int source) {
        userid = uid;
        gold = igold;
        gameid = igame;
        tableid = itableid;
        markunit = imark;
        goldtransfer = itranfer;
        datetransfer = dtranfer;
        this.source = source;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public int getTableid() {
        return tableid;
    }

    public void setTableid(int tableid) {
        this.tableid = tableid;
    }

    public int getMarkunit() {
        return markunit;
    }

    public void setMarkunit(int markunit) {
        this.markunit = markunit;
    }

    public int getGoldtransfer() {
        return goldtransfer;
    }

    public void setGoldtransfer(int goldtransfer) {
        this.goldtransfer = goldtransfer;
    }

    public Date getDatetransfer() {
        return datetransfer;
    }

    public void setDatetransfer(Date datetransfer) {
        this.datetransfer = datetransfer;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

}
