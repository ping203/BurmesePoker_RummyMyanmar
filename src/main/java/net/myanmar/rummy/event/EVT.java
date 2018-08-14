/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.event;

/**
 *
 * @author hoangchau
 */
public class EVT {
    public static final String OBJECT_FINISH            = "finish";
    public static final String OBJECT_AUTO_START        = "autoStartGame";
    public static final String OBJECT_AUTO_READY        = "autoReady";
    public static final String OBJECT_TAKE_CARD_DECK    = "bc";
    public static final String OBJECT_TAKE_CARD_PLACES  = "ac";
    public static final String OBJECT_DISCARD           = "dc";
    public static final String OBJECT_FOLD_CARD         = "upbo";
    public static final String OBJECT_TIMEOUT           = "timeout";
    public static final String OBJECT_PLAYER_DISCONNECT = "disltable";
    public static final String OBJECT_TURN_DISCONNECT   = "playdis";
    public static final String OBJECT_BOT_SHOT          = "botshot";
    public static final String OBJECT_NOTICE_DECLARE    = "nd";
    public static final String OBJECT_PLAYER_DECLARE    = "declare";
    public static final String OBJECT_REMOVE_BOT        = "removebot";
    public static final String OBJECT_CHECK_GET_BOT     = "checkGetBot";
    public static final String OBJECT_GET_BOT           = "getbot";
    public static final String OBJECT_READY_BOT         = "readyBot";
    
    public static final String DATA_READY_TABLE         = "rtable";
    public static final String DATA_START_GAME          = "sgame";
    public static final String DATA_TAKE_CARD_DECK      = "bc";
    public static final String DATA_DISCARD             = "dc";
    public static final String DATA_TAKE_CARD_PLACES    = "ac";
    public static final String DATA_NOTICE_DECLARE      = "nd";
    public static final String DATA_PLAYER_DECLARE      = "declare";
    public static final String DATA_FOLD_CARD           = "bd";
    @Deprecated
    public static final String DATA_HACK                = "info";
    public static final String DATA_CHAT_TABLE          = "chattable";
    public static final String DATA_AUTO_EXIT           = "autoExit";
    public static final String DATA_TIP_DEALER          = "tip";
    public static final String DATA_AMUVIP              = "amuvip";
    public static final String DATA_AGIAP               = "ag_iap";
    public static final String DATA_KICK_TABLE          = "ktable";
    public static final String DATA_CONFIRM_DECLARE     = "cfd";
    @Deprecated
    public static final String DATA_UVIP                = "uvip";
    
    public static final String CLIENT_RECONNECT         = "rjtable";
    public static final String CLIENT_CREATE_TABLE      = "ctable";
    public static final String CLIENT_OTHER_JOIN        = "stable";
    public static final String CLIENT_JOIN_TABLE        = "jtable";
    public static final String CLIENT_COUNTDOWN_START   = "timeToStart";
    public static final String CLIENT_VIEW_TABLE        = "vtable";
    public static final String CLIENT_SYSTEM_CHAT       = "schat";
    public static final String CLIENT_READY             = "rtable";
    public static final String CLIENT_LEFT              = "ltable";
    public static final String CLIENT_CHANGE_OWNER      = "cctable";
    public static final String CLIENT_FOLD_CARD              = "bd";
    public static final String CLIENT_DEAL              = "lc";
    public static final String CLIENT_TAKE_CARD_DECK    = "bc";
    public static final String CLIENT_DISCARD           = "dc";
    public static final String CLIENT_WINNER            = "winner";
    public static final String CLIENT_TAKE_CARD_PLACE   = "ac";
    public static final String CLIENT_NOTICE_DECLARE    = "nd";
    public static final String CLIENT_AM                = "am";
    public static final String CLIENT_AGIAP             = "ag_iap" ;
    public static final String CLIENT_AUTO_EXIT         = "autoExit";
    public static final String CLIENT_TIP_DEALER        = "tip";
    public static final String CLIENT_FINISH            = "finish";
    public static final String CLIENT_ACTION_ERROR      = "ace";
    public static final String CLIENT_CONFIRM_DECLARE   = "cfd";
    public static final String CLIENT_DECLARED          = "declare";
    
    
    
    
}
