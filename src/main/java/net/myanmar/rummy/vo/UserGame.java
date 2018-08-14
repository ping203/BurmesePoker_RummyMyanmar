package net.myanmar.rummy.vo;


public class UserGame {

    private Integer Userid;
    
    private String Username;
    
    private String Tinyurl;
    
    private String sIP;
    
    private long AG;
    
    private long Diamond;
    
    private Integer LQ;
    
    private Integer VIP;
    
    private Integer Gender;
    
    private Integer Operatorid;
    
    private Integer pid;
    
    private Integer LevelId;
    
    private Integer RoomId;
    
    private Integer TableId;
    
    private Integer GameId;
    
    private long CreateTime;
    
    private boolean Nap;
    
    private Integer GameCount;
    
    private long agWin;
    
    private long agLose;
    
    private Integer A;
    
    private Integer UnlockPass;
    
    private Long Facebookid;
    
    private Long Googleid;
    
    private Integer Source; //User den tu LQ hoac 68
    
    private short Usertype;
    
    private boolean AutoFill;
    
    private boolean AutoTopOff;
    
    private boolean isBocBai;
    
    private boolean isDeclared;
    
    private int diemThua;
    
    private boolean isNotiDeclared;

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

    public long getDiamond() {
        return Diamond;
    }

    public void setDiamond(long diamond) {
        Diamond = diamond;
    }

    public Integer getSource() {
        return Source;
    }

    public void setSource(Integer source) {
        Source = source;
    }

    public Long getGoogleid() {
        return Googleid;
    }

    public void setGoogleid(Long googleid) {
        Googleid = googleid;
    }

    public Long getFacebookid() {
        return Facebookid;
    }

    public void setFacebookid(Long facebookid) {
        Facebookid = facebookid;
    }

    public Integer getUnlockPass() {
        return UnlockPass;
    }

    public void setUnlockPass(Integer unlockPass) {
        UnlockPass = unlockPass;
    }

    public Integer getA() {
        return A;
    }

    public void setA(Integer a) {
        A = a;
    }

    public Integer getOperatorid() {
        return Operatorid;
    }

    public void setOperatorid(Integer operatorid) {
        Operatorid = operatorid;
    }

    public Integer getGameId() {
        return GameId;
    }

    public void setGameId(Integer gameId) {
        GameId = gameId;
    }

    public long getAgWin() {
        return agWin;
    }

    public void setAgWin(long ag) {
        this.agWin = ag;
    }

    public long getAgLose() {
        return agLose;
    }

    public void setAgLose(long ag) {
        this.agLose = ag;
    }

    public Integer getUserid() {
        return Userid;
    }

    public void setUserid(Integer userid) {
        Userid = userid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getTinyurl() {
        return Tinyurl;
    }

    public void setTinyurl(String tinyurl) {
        Tinyurl = tinyurl;
    }

    public String getsIP() {
        return sIP;
    }

    public void setsIP(String sIP) {
        this.sIP = sIP;
    }

    public long getAG() {
        return AG;
    }

    public void setAG(long aG) {
        AG = aG;
    }

    public Integer getLQ() {
        return LQ;
    }

    public void setLQ(Integer lQ) {
        LQ = lQ;
    }

    public Integer getGender() {
        return Gender;
    }

    public void setGender(Integer gender) {
        Gender = gender;
    }

    public Integer getVIP() {
        return VIP;
    }

    public void setVIP(Integer vIP) {
        VIP = vIP;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getLevelId() {
        return LevelId;
    }

    public void setLevelId(Integer levelId) {
        LevelId = levelId;
    }

    public Integer getRoomId() {
        return RoomId;
    }

    public void setRoomId(Integer roomId) {
        RoomId = roomId;
    }

    public Integer getTableId() {
        return TableId;
    }

    public void setTableId(Integer tableId) {
        TableId = tableId;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public boolean isNap() {
        return Nap;
    }

    public void setNap(boolean nap) {
        Nap = nap;
    }

    public Integer getGameCount() {
        return GameCount;
    }

    public void setGameCount(Integer gameCount) {
        GameCount = gameCount;
    }

    public short getUsertype() {
        return Usertype;
    }

    public void setUsertype(short usertype) {
        Usertype = usertype;
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

}
