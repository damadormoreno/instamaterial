package com.softonic.instamaterial.data.repository.user;

/**
 * Created by alnit on 17/02/2018.
 */

public class UserData {
    private String displayName;
    private String photoUrl;

    @SuppressWarnings("unused")
    public UserData(){}

    public UserData(String displayName, String photoUrl) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
