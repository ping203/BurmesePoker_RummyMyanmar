package net.myanmar.rummy.vo;

public class ObjLogPlayer {

    private int pid;
    private short gameID;
    private int mark;
    private long time;
    private int source;
    
    private long winG;  
    private String name;
    private long firstG;
    private long lastG;
    private short bot;
    
    private short operator;
   
    
    //void LogPlayer(int userid,int GameId,int iLevel,int iWin,java.sql.Date dtTime, int source, int iWinMark, int deviceid, int diamondType) ;

    public ObjLogPlayer(int pid, int source, String name, long firstG, short bot, short gameID, int mark, short operator){
        this.pid = pid;
        this.name = name;
        this.firstG = firstG;
        this.source = source;
        this.setBot(bot);
        this.gameID = gameID;
        this.mark = mark;
        this.operator = operator;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFirstG() {
        return firstG;
    }

    public void setFirstG(long firstG) {
        this.firstG = firstG;
    }

    public long getLastG() {
        return lastG;
    }

    public void setLastG(long lastG) {
        this.lastG = lastG;
    }

	public int getBot() {
		return bot;
	}

	public void setBot(short bot) {
		this.bot = bot;
	}

	public short getGameID() {
		return gameID;
	}

	public void setGameID(short gameID) {
		this.gameID = gameID;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getWinG() {
		return winG;
	}

	public void setWinG(long winG) {
		this.winG = winG;
	}

	public short getOperator() {
		return operator;
	}

	public void setOperator(short operator) {
		this.operator = operator;
	}

}

