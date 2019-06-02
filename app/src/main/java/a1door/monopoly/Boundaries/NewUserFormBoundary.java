package a1door.monopoly.Boundaries;


import a1door.monopoly.Entities.UserEntity;
import a1door.monopoly.Entities.UserRole;

public class NewUserFormBoundary {
	private String email;
	private String role;
	private String username;
	private String avatar;
	
public NewUserFormBoundary() {
}





public NewUserFormBoundary(String email, String role, String username, String avatar) {
	super();
	this.email = email;
	this.role = role;
	this.username = username;
	this.avatar = avatar;
}



public UserEntity toEntity()
{
	UserEntity entity = new UserEntity();
	
	if(this.email!=null)
		entity.setUserEmail(this.email);

	if (this.username != null)
		entity.setUsername(this.username);

	if (this.avatar != null)
		entity.setAvatar(this.avatar);

	if (this.role != null)
		entity.setRole(UserRole.valueOf(this.role));

	return entity;
}





public String getEmail() {
	return email;
}





public void setEmail(String email) {
	this.email = email;
}





public String getRole() {
	return role;
}





public void setRole(String role) {
	this.role = role;
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





@Override
public String toString() {
	return "NewUserFormBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar
			+ "]";
}



}
