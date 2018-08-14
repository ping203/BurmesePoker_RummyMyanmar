package net.myanmar.rummy.utils;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

//import net.kalaha.json.ActionTransformer;
import com.cubeia.firebase.api.action.GameDataAction;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class GameUtil {
    //@Inject
//    public static final String strTableError = "Table is invalid, you should not join this table.";
//    public static final String strNotGame = "You have not login to game Remi.";
//    public static final String strInGame = "You are in table.";
//    public static final String strUnderVip = "Your VIP is not enough to join this table.";
//    public static final String strUnderChip = "Your Chip is not enough to join this table.";
//    
//    public static final String strJoinTable = " join table.";
//    public static final String strLeftTable = " get out of table.";
//    public static final String strSystem = "System";
//    public static final String strCreate_UnderAG = "Your Chip is not enough to create table.";
//    public static final String strCreate_0AG = "You must not create table with negative stake.";
//    public static final String strConnectGame = "Lost connection to server, please reconnect.";
//    public static final String strNotAGChip = "Your Chip is not enough to join table, please try again.";
//    public static final String strKick_Err1_TH = "You will be out of this table after the game finish.";
//    public static final String strKick_Err2_TH = "Do not kick player in this table.";
//    public static final String strKick_Err3_TH = "You have not enough Chip to continue playing in this stake.";
//    public static final String strTipNotEnoughAG_TH = "Your Chip is not enough to Tip";
//    public static final String DIS_CARD_JUST_TAKE_PLACE = "You can't discard a card which you've just take";

    public static final String strTableError = "Meja tak sesuai, anda tidak dapat main di meja ini.";
    public static final String strNotGame = "Anda belum login ke game Remi.";
    public static final String strInGame = "Anda sedang di meja.";
    public static final String strUnderVip = "VIP anda tidak cukup untuk bermain di meja.";
    public static final String strUnderChip = "Chip anda tidak cukup untuk bermain di meja.";
    
    public static final String strJoinTable = " Masuk meja.";
    public static final String strLeftTable = " Keluar dari meja.";
    public static final String strSystem = "Sistem";
    public static final String strCreate_UnderAG = "Chip anda tidak cukup untuk membuat meja.";
    public static final String strCreate_0AG = "Anda tidak dapat membuat meja dengan taruhan negatif.";
    public static final String strConnectGame = "Hilang hubungan ke server, mohon koneksi lagi.";
    public static final String strNotAGChip = "Chip anda tidak cukup untuk main di meja, mohon coba lagi.";
    public static final String strKick_Err1_TH = "Anda akan keluar meja setelah permainan selesai.";
    public static final String strKick_Err2_TH = "Jangan menendang pemain di meja.";
    public static final String strKick_Err3_TH = "Anda tidak memiliki cukup chip untuk bermain di taruhan ini.";
    public static final String strTipNotEnoughAG_TH = "Chip anda tidak cukup untuk Tip";
    public static final String DIS_CARD_JUST_TAKE_PLACE = "Anda tidak dapat membuang kartu yang baru saja diambil";   
    public static final String SELECT_ROOM_TOO_FAST = "May bam cham thoi con"; 
    //public static final String strOverVip = "Please choose a higher stake";
    //public static final String strUnderAG = "Your Chip is not enough to join this table.";
    //public static final String strOverLevel = "Please choose a higher stake";
    //public static final String strSetting_UnderVip = " Not enoungh VIP, please choose lower VIP or ";
    // public static final String strSetting_UnderAG = " Not enoungh Chip, please choose lower stake or  ";
    //public static final String strSetting_Surfix = "Get out of this table before resetting table."; 
    
    public static final Random random = new Random();
    
    public static final Gson gson = new Gson();

    public static long getDayBetween2Dates(Date dt1, Date dt2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dt1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dt2);
        return (cal1.getTime().getTime() - cal2.getTime().getTime()) / (24 * 3600 * 1000);
    }

    public static long getSecondsBetween2Dates(Date dt1, Date dt2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dt1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dt2);
        return (cal1.getTime().getTime() - cal2.getTime().getTime()) / (1000);
    }

    public static GameDataAction toDataAction(int playerId, int tableId, String action) {
        //String s = toString(action);
        GameDataAction gda = new GameDataAction(playerId, tableId);
        try {
            gda.setData(ByteBuffer.wrap(action.getBytes("UFT-8")));
        } catch (UnsupportedEncodingException e) {

        }
        return gda;
    }

    public static GameDataAction toDataAction(int playerId, int tableId, Object action){
        //String s = toString(action);

        GameDataAction gda = new GameDataAction(playerId, tableId);
        try {
            gda.setData(ByteBuffer.wrap(gson.toJson(action).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            
        }

        return gda;
    }

    public static String formatPositiveAG(int pag) {
        int d = pag % 1000;
        int r = pag / 1000;
        String ds = "";
        if (d < 10) {
            ds = "00" + String.valueOf(d);
        } else if (d < 100) {
            ds = "0" + String.valueOf(d);
        } else {
            ds = String.valueOf(d);
        }

        if (r >= 1) {
            return formatPositiveAG(r) + "," + ds;
        } else {
            return String.valueOf(d);
        }
    }

    public static String formatAG(int ag) {
        if (ag >= 0) {
            return formatPositiveAG(ag);
        } else {
            return "-" + formatPositiveAG(-ag);
        }
    }
}
