package a1door.monopoly.Boundaries;

public class UserKey {
    private String smartspace;
    private String email;

    public UserKey() {

    }


    public UserKey(String smartspace, String email) {
        super();
        this.smartspace = smartspace;
        this.email = email;
    }




    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getSmartspace() {
        return smartspace;
    }

    public void setSmartspace(String smartspace) {
        this.smartspace = smartspace;
    }



}
