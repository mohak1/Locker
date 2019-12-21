package com.fablab.locker;

import com.firebase.client.Firebase;

public class Helper {

    public static Firebase mRootRef, mUserRef, mLockerRef;

    public static final String ROOT_URL = "https://digilocker-dc8c1.firebaseio.com/";
    public static final String LOCKER_URL = "https://digilocker-dc8c1.firebaseio.com/Locker";
    public static final String  USER_URL = "https://digilocker-dc8c1.firebaseio.com/User";


    //To establish a link to the firebase tree
    public static void firebaseInit() {
        mRootRef = new Firebase(ROOT_URL);
        mUserRef = new Firebase(USER_URL);
        mLockerRef = new Firebase(LOCKER_URL);
    }

}
