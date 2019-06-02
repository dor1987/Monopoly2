package a1door.monopoly.Entities;

import java.io.Serializable;

public class UserEntity implements SmartspaceEntity<String> ,Serializable {
    private String userSmartspace;
    private String userEmail;
    private String username;
    private String avatar;
    private UserRole role;
    private long points;
    // private String userKey;

    public UserEntity() {
    }

    public UserEntity(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserEntity(String userSmartspace, String userEmail, String username, String avatar, UserRole role,
                      long points) {
        this.userSmartspace = userSmartspace;
        this.userEmail = userEmail;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.points = points;

    }

    public String getUserSmartspace() {
        return userSmartspace;
    }

    public void setUserSmartspace(String userSmartspace) {
        this.userSmartspace = userSmartspace;
    }

    public String getUserEmail() {
        /*
         * if(getKey()==null) { return userEmail; } else if(userEmail==null) { String[]
         * tempStringArrayHolder = getKey().split("\\|"); return
         * tempStringArrayHolder[1]; } else if(getKey().isEmpty() ||
         * !userEmail.isEmpty()) { return userEmail; } else { throw new
         * RuntimeException("there is no eMail exsists for the user" + getUsername()); }
         */
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public String getKey() {
        return this.userSmartspace + "|" + this.userEmail;
    }

    @Override
    public void setKey(String key) {
        String[] tempStringArrayHolder = key.split("\\|");
        this.userSmartspace = tempStringArrayHolder[0];
        this.userEmail = tempStringArrayHolder[1];

    }

}
