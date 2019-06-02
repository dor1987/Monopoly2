package a1door.monopoly.Boundaries;

import a1door.monopoly.Entities.UserEntity;
import a1door.monopoly.Entities.UserRole;

public class UserBoundary {
    private UserKey key;
    private String username;
    private String avatar;
    private String role;
    private long points;

    public UserBoundary() {

    }

    public UserBoundary(String username, String avatar, String role, long points) {
        super();
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.points = points;
    }

    public UserBoundary(UserEntity entity) {
        if (entity.getUserEmail() != null && entity.getUserSmartspace() != null)
        {

            this.key = new UserKey(entity.getUserSmartspace(), entity.getUserEmail());

        }
        else
            this.key = null;


        this.username = entity.getUsername();

        this.avatar = entity.getAvatar();

        if(entity.getRole()!=null)
            this.role = entity.getRole().name();
        else
            this.role = null;

        this.points = entity.getPoints();
    }

    public UserKey getKey() {
        return key;
    }

    public void setKey(UserKey key) {
        this.key = key;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }


    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();


        if(key!=null)
        {
            if(this.key.getSmartspace()!=null)
                entity.setUserSmartspace(this.key.getSmartspace());

            if(this.key.getEmail()!=null)
                entity.setUserEmail(this.key.getEmail());
        }
        if (this.username != null)
            entity.setUsername(this.username);

        if (this.avatar != null)
            entity.setAvatar(this.avatar);

        if (this.role != null)
            entity.setRole(UserRole.valueOf(this.role));

        entity.setPoints(this.points);

        return entity;
    }

    @Override
    public String toString() {
        return "UserBoundary [key=" + key + ", username=" + username + ", avatar=" + avatar + ", role=" + role
                + ", points=" + points + "]";
    }


}
