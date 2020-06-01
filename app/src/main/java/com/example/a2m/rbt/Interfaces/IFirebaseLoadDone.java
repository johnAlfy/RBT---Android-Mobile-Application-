package com.example.a2m.rbt.Interfaces;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadLiveTripsDone(List<String> lstEmail);
    void onFirebaseLoadFailed(String msg);
}
