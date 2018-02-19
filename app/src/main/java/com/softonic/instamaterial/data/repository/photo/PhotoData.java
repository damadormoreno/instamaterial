package com.softonic.instamaterial.data.repository.photo;

/**
 * Created by alnit on 15/02/2018.
 */

public class PhotoData {
    private String userId;
    private String sourceUrl;
    private String description;

    @SuppressWarnings("unused")
    PhotoData(){}

    public PhotoData(String userId, String sourceUrl, String description) {
        this.userId = userId;
        this.sourceUrl = sourceUrl;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValid() {
        return userId !=null && sourceUrl !=null && description != null;
    }
}
