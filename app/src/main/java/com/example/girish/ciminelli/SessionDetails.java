package com.example.girish.ciminelli;

/**
 * Created by girish on 9/28/15.
 */

/* will keep track of user details and assets viewed throughout the session */
public class SessionDetails {


    static String username = "";
    static String password = "";

    static String assetCode = "";

    static String ip = "http://192.168.43.164:8888/";

    /* setting all permissions to true as default */

    static boolean modifyComments = true;

    static boolean modifyStages = true;

    static boolean qualityGuy = true;

    /* Constants to fill the list adapter */
    public static final String FIRST_COLUMN = "First";
    public static final String SECOND_COLUMN = "Second";
    public static final String THIRD_COLUMN = "Third";
    public static final String FOURTH_COLUMN = "Fourth";
    public static final String FIFTH_COLUMN = "Fifth";



}