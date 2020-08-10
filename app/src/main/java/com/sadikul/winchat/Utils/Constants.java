package com.sadikul.winchat.Utils;

/**
 * Created by ASUS on 27-Nov-17.
 */

public class Constants {
    public static final String BaseURL="http://sadikulsathi.com/naptel/";//
    public static final String Registration="api/register";
    public static final String login="api/login";
    public static final String TermsNConditoinTAG="terms adn condition";
    public static final String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String LoginPresenterTAG="LoginPresenter";
    public static final String SignupTAG="Signup_activity";
    public static final String registrationPresenterTAG="registrationPresenter";
    public static final String isLoginCheck="loginCheck";
    public static final String NaptelPreference="com.sadikul.napter.Sharedpreference";


    //name of the child of database
    public static final String usersDatabaseRef ="Users";
    public static final String nameChild="name";
    public static final String statusChild="status";
    public static final String imageChild="image";
    public static final String thumbnailImageChild="thumbnailImage";

//database for managin friend request
    public static final String friendReqDatabaseRef ="FriendRequest";
    public static final String requestType ="request_type";
    public static final String sent ="sent";
    public static final String received ="received";
    public static final String friend ="friend";
    public static final String requestSentSuccessfully ="Request sent succesfully...";
    public static final String requestCancelSuccessfully ="Request cancel succesfully...";
    public static final String requestSentFailed ="Request sent failed...";
    public static final String cancelFriendRequest ="Cancel friend request";
    public static final String sentFriendRequest ="Send friend request";
    public static final String acceptFriendRequest ="Accept friend request";

    //database Friend
    public static final String friendsDatabaseRef ="Friends";

    public static final String unfriendRequest ="Unfriend";

    //database Notifications
    public static final String notificationsDatabaseRef ="Notifications";
    public static final String device_token ="device_token";

}
