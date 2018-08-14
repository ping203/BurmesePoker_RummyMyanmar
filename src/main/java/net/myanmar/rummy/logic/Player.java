/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import net.myanmar.rummy.vo.UserGame;

import net.myanmar.rummy.utils.GameUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author UserXP
 */
public class Player implements Serializable {

    private List<List<Integer>> listGroup;
    private TurnStatus turnStatus;
//    private int cardPlaceId;
    
    private int fund;

    private int score = 0;
    
    private long goldResult;
    
    private int numTake;
    
    private boolean firstTakeDeck = true;
    
    public Player() {
        listGroup = new ArrayList<>();
    }

    public Player(UserGame ui) {
        isStart = false;
        setUserid(ui.getUserid());
        setUsername(ui.getUsername());
        setTinyurl(ui.getTinyurl());
        setsIP(ui.getsIP());
        setAG(ui.getAG());
        setLQ(ui.getLQ());
        setVIP(ui.getVIP());
        setGender(ui.getGender());
        setPid(ui.getPid());
        setLevelId(ui.getLevelId());
        setRoomId(ui.getRoomId());
        setCreateTime(new Date(ui.getCreateTime()));
        setOperatorId(ui.getOperatorid());
        setAvatar(ui.getA());
        setActive(false);
        setPos(0);
        setDisconnect(false);
        setNap(ui.isNap());
        setFacebookId(ui.getFacebookid());
        setGoogleId(ui.getGoogleid());
        setSource(ui.getSource());
        setGameCount(ui.getGameCount());
        setUsertype(ui.getUsertype());
        if (ui.getUsertype() > 10) {
            int rd = GameUtil.random.nextInt(4) + 7;
            setRdPlay(rd);
        }
        setNumberAuto(0);
        setAutoFill(ui.isAutoFill());
        setAutoTopOff(ui.isAutoTopOff());
        isAuto = false;
        ArrCard = new ArrayList<Card>();
//        listCardDouble = new ArrayList<List<Double>>();
        listGroup = new ArrayList<>();
        setAgWin(ui.getAgWin());
        setAgLose(ui.getAgLose());
        setIsBocBai(ui.getIsBocBai());
        setIsDeclared(ui.getIsDeclared());
        setDiemThua(ui.getDiemThua());
        setIsNotiDeclared(ui.getIsNotiDeclared());
        setCountPlayed(0);
    }

    public boolean isFirstTakeDeck() {
        return firstTakeDeck;
    }

    public void setFirstTakeDeck(boolean firstTakeDeck) {
        this.firstTakeDeck = firstTakeDeck;
    }

    

    public int getFund() {
        return fund;
    }

    public void setFund(int fund) {
        this.fund = fund;
    }

    public int getNumTake() {
        return numTake;
    }

    public void setNumTake(int numTake) {
        this.numTake = numTake;
    }

    

    public TurnStatus getTurnStatus() {
        return turnStatus;
    }

    public void setTurnStatus(TurnStatus turnStatus) {
        this.turnStatus = turnStatus;
    }

    public List<List<Integer>> getListGroup() {
        return listGroup;
    }

    public void setListGroup(List<List<Integer>> listGroup) {
        this.listGroup = listGroup;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    

    public ItemPlayer getItemPlayer() {
        ItemPlayer ret = new ItemPlayer();
        ret.setId(this.Pid);
        ret.setN(this.Username);
        ret.setAG(this.AG);
        ret.setLQ(this.LQ);
        ret.setVIP(this.VIP);
        ret.setG(this.Gender);
        ret.setUrl(this.Tinyurl);
        ret.setsIP(this.sIP);
        ret.setIsStart(this.isStart);
        ret.setAv(this.Avatar);
        ret.setFId(this.FacebookId);
        ret.setGId(this.GoogleId);
        ret.setUserType(this.getUsertype());
        if ((GameUtil.getDayBetween2Dates(new Date(), CreateTime) > 1) || (this.VIP > 0)) {
            ret.setIK(0);
        } else {
            ret.setIK(1);
        }
        ret.setTimeToStart(this.timeToStart);
        return ret;
    }

    public ItemVPlayer getItemVPlayer(int type) {
        ItemVPlayer ret = new ItemVPlayer();
        ret.setId(this.Pid);
        ret.setN(this.Username);
        ret.setAG(this.AG);
        ret.setA(this.Active);
        ret.setsIP(this.sIP);
        ret.setLQ(this.LQ);
        ret.setVIP(this.VIP);
        ret.setG(this.Gender);
        ret.setUrl(this.Tinyurl);
        ret.setIsStart(this.isStart);
        ret.setAv(this.Avatar);
        ret.setFId(this.FacebookId);
        ret.setGId(this.GoogleId);
        ret.setUserType(this.getUsertype());
        if ((GameUtil.getDayBetween2Dates(CreateTime, new Date()) > 1) || (this.VIP > 0)) {
            ret.setIK(0);
        } else {
            ret.setIK(1);
        }
        //if (!this.isDeclared) {
            List<int[]> arr = new ArrayList<int[]>();
            if (type == 1) {
                arr.add(getCardTrans(true));
                ret.setArr(arr);
            } else {
                arr.add(getCardTrans(false));
                ret.setArr(arr);
            }
//        } else {
//            List<int[]> larr = new ArrayList<>();
//            for (int i = 0; i < listGroup.size(); i++) {
//                List<Integer> list = listGroup.get(i);
//                if (list.size() > 0) {
//                    int[] arr = new int[list.size()];
//                    for (int j = 0; j < list.size(); j++) {
//                        arr[j] = list.get(j);
//                    }
//
//                    larr.add(arr);
//                }
//
//            }
//            ret.setArr(larr);
//        }
        ret.setIsBocBai(this.isBocBai);
        ret.setIsDeclared(this.isDeclared);
        ret.setDiemThua(this.diemThua);
        ret.setAgWin(this.AgWin);
        ret.setAgLose(this.AgLose);
        return ret;
    }

    public int[] getCardTrans(boolean isAll) {
        if (isAll) {
            int[] arr = new int[ArrCard.size()];
            for (int i = 0; i < ArrCard.size(); i++) {
                arr[i] = ArrCard.get(i).getI();
            }
            return arr;
        } else {
            int[] arr = new int[ArrCard.size()];
            if (ArrCard.size() > 0) {
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = 0;
                }
            }
            return arr;
        }
    }

    private boolean isStart;
    private int Pid;

    private String Username;
    private String Tinyurl;
    private Integer Userid;
    private Long AG;
    private Integer LQ;
    private Integer VIP;
    private Integer Gender;
    private String sIP;

    private Date CreateTime;
    private boolean isOnline;
    private int LevelId;
    private int RoomId;
    private int OperatorId;
    private short Usertype;
    private boolean AutoFill; // Tu dong lay them tien neu du de tiep tuc choi
    private boolean AutoTopOff; // Tien mang vao khong bao gio duoi muc ban dau
    private int timeToStart;
    private long AgWin;
    private long AgLose;
    private boolean isBocBai;
    private boolean isDeclared;
    private int diemThua;
    private boolean isNotiDeclared;
    private int countPlayed;
    private int rdPlay;

    public void setAgWin(long ag) {
        this.AgWin = ag;
    }

    public long getAgWin() {
        return AgWin;
    }

    public void setAgLose(long ag) {
        this.AgLose = ag;
    }

    public long getAgLose() {
        return AgLose;
    }

    public boolean isAutoFill() {
        return AutoFill;
    }

    public void setAutoFill(boolean AutoFill) {
        this.AutoFill = AutoFill;
    }

    public boolean isAutoTopOff() {
        return AutoTopOff;
    }

    public void setAutoTopOff(boolean AutoTopOff) {
        this.AutoTopOff = AutoTopOff;
    }

    public int getOperatorId() {
        return OperatorId;
    }

    public void setOperatorId(int operatorId) {
        OperatorId = operatorId;
    }

    public boolean getIsBocBai() {
        return isBocBai;
    }

    public void setIsBocBai(boolean bocbai) {
        this.isBocBai = bocbai;
    }

    public boolean getIsDeclared() {
        return isDeclared;
    }

    public void setIsDeclared(boolean isDeclared) {
        this.isDeclared = isDeclared;
    }

    public int getDiemThua() {
        return diemThua;
    }

    public void setDiemThua(int diem) {
        this.diemThua = diem;
    }

    public boolean getIsNotiDeclared() {
        return isNotiDeclared;
    }

    public void setIsNotiDeclared(boolean isDeclared) {
        this.isNotiDeclared = isDeclared;
    }

    public int getCountPlayed() {
        return this.countPlayed;
    }

    public void setCountPlayed(int count) {
        this.countPlayed = count;
    }

    public int getRdPlay() {
        return this.rdPlay;
    }

    public void setRdPlay(int play) {
        this.rdPlay = play;
    }

    private List<Card> ArrCard;
    private boolean Disconnect;
    private int Pos;
    /*
        * player úp bai thi ko dc danh tiep
    **/
    private boolean Active = true;
    private boolean isNap;
    private int GameCount;
    private int Avatar;
    private long FacebookId; // ID Facebook
    private long GoogleId; // ID Google
    private int Source; // 1-Den tu LQ, 2-Den tu 68
    private int AutoAction;// 1 - Tu dong xem bai, 2 - Tu dong Up, 3 - Tu dong theo bat ky
    private boolean Autoexit; // Tu dong thoat ban khi het van.
    private int numberAuto; // So lan auto lien tiep
    private boolean isAuto; // co Auto trong van
//    private List<List<Double>> listCardDouble;
    private int countAFK;

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public int getNumberAuto() {
        return numberAuto;
    }

    public void setNumberAuto(int numberAuto) {
        this.numberAuto = numberAuto;
    }

    public boolean isAutoexit() {
        return Autoexit;
    }

    public void setAutoexit(boolean autoexit) {
        Autoexit = autoexit;
    }

    public int getAutoAction() {
        return AutoAction;
    }

    public void setAutoAction(int autoAction) {
        AutoAction = autoAction;
    }

    public int getSource() {
        return Source;
    }

    public void setSource(int source) {
        Source = source;
    }

    public long getFacebookId() {
        return FacebookId;
    }

    public void setFacebookId(long facebookId) {
        FacebookId = facebookId;
    }

    public long getGoogleId() {
        return GoogleId;
    }

    public void setGoogleId(long googleId) {
        GoogleId = googleId;
    }

    public Integer getAvatar() {
        return Avatar;
    }

    public void setAvatar(Integer avatar) {
        Avatar = avatar;
    }

    public boolean isNap() {
        return isNap;
    }

    public void setNap(boolean isNap) {
        this.isNap = isNap;
    }

    public int getGameCount() {
        return GameCount;
    }

    public void setGameCount(int gameCount) {
        GameCount = gameCount;
    }

    public String getsIP() {
        return sIP;
    }

    public void setsIP(String sIP) {
        this.sIP = sIP;
    }

    public int getPos() {
        return Pos;
    }

    public void setPos(int pos) {
        Pos = pos;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public boolean isDisconnect() {
        return Disconnect;
    }

    public void setDisconnect(boolean disconnect) {
        Disconnect = disconnect;
    }

    public Integer getVIP() {
        return VIP;
    }

    public void setVIP(Integer vIP) {
        VIP = vIP;
    }

    public void setGender(Integer gender) {
        Gender = gender;
    }

    public Integer getGender() {
        return Gender;
    }

    public Long getAG() {
        return AG;
    }

    public void setAG(Long aG) {
        AG = aG;
    }

    public Integer getLQ() {
        return LQ;
    }

    public void setLQ(Integer lQ) {
        LQ = lQ;
    }

    public List<Card> getArrCard() {
        return ArrCard;
    }

    public void setArrCard(List<Card> arrCard) {
        ArrCard = arrCard;
    }

    /**
     * Get the value of RoomId
     *
     * @return the value of RoomId
     */
    public int getRoomId() {
        return RoomId;
    }

    /**
     * Set the value of RoomId
     *
     * @param RoomId new value of RoomId
     */
    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
    }

    /**
     * Get the value of LevelId
     *
     * @return the value of LevelId
     */
    public int getLevelId() {
        return LevelId;
    }

    /**
     * Set the value of LevelId
     *
     * @param LevelId new value of LevelId
     */
    public void setLevelId(int LevelId) {
        this.LevelId = LevelId;
    }

    /**
     * Get the value of isOnline
     *
     * @return the value of isOnline
     */
    public boolean isIsOnline() {
        return isOnline;
    }

    /**
     * Set the value of isOnline
     *
     * @param isOnline new value of isOnline
     */
    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * Get the value of CreateTime
     *
     * @return the value of CreateTime
     */
    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date CreateTime) {
        this.CreateTime = CreateTime;
    }

    public Integer getUserid() {
        return Userid;
    }

    public void setUserid(Integer userid) {
        this.Userid = userid;
    }

    public String getTinyurl() {
        return Tinyurl;
    }

    public void setTinyurl(String tinyurl) {
        this.Tinyurl = tinyurl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public int getPid() {
        return Pid;
    }

    public void setPid(int Pid) {
        this.Pid = Pid;
    }

    public boolean isIsStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    private UUID idTimeout = null;

    public void setTimeoutActionId(UUID id) {
        // TODO Auto-generated method stub
        idTimeout = id;
    }

    public UUID getTimeoutActionId() {
        // TODO Auto-generated method stub
        return idTimeout;
    }

    public short getUsertype() {
        return Usertype;
    }

    public void setUsertype(short usertype) {
        Usertype = usertype;
    }

    public void setTimeToStart(int time) {
        this.timeToStart = time;
    }

    public int getTimeToStart() {
        return timeToStart;
    }

    public int getCountAFK() {
        return countAFK;
    }

    public void setCountAFK(int count) {
        this.countAFK = count;
    }

//    public int getCardPlaceId() {
//        return cardPlaceId;
//    }
//
//    public void setCardPlaceId(int cardPlaceId) {
//        this.cardPlaceId = cardPlaceId;
//    }

    public long getGoldResult() {
        return goldResult;
    }

    public void setGoldResult(long goldResult) {
        this.goldResult = goldResult;
    }
    
    
    

    public void reset() {
        listGroup.clear();
        turnStatus = TurnStatus.NULL;
        ArrCard.clear();
        Active = true;
//        cardPlaceId = 0;
        idTimeout = null;
        numberAuto++;
        fund = RummyBoard.FUND;
        numTake = 0;
        firstTakeDeck = true;
    }


    public String getDisplayCard() {
        String display = "[";
        for (int i = 0; i < ArrCard.size(); i++) {
            display += ArrCard.get(i).getI() + " " + ArrCard.get(i).toString();
            if (i != ArrCard.size() - 1) {
                display += ",";
            }
        }
        display += "]";
        return display;
    }
}
