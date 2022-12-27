package com.if5b.iklanku.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Post {
    private int id;
    private String content;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("created_date")
    private String createdDate;
    @SerializedName("modified_date")
    private String modifiedDate;
    private String username;

    public Post() {
    }

    protected Post(Parcel in) {
        id = in.readInt();
        content = in.readString();
        userId= in.readString();
        createdDate = in.readString();
        modifiedDate = in.readString();
        username = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(userId);
        dest.writeString(createdDate);
        dest.writeString(modifiedDate);
        dest.writeString(username);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
