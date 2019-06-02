package a1door.monopoly.Entities;

import java.util.Date;
import java.util.Map;

public class ElementEntity implements SmartspaceEntity<String> {

    private String elementSmartspace;
    private String elementId;
    private Location location;
    private String name;
    private String type;
    private Date creationTimestamp;
    private boolean expired;
    private String creatorSmartspace;
    private String creatorEmail;
    private Map<String, Object> moreAttributes;

    public ElementEntity() {
        this.creationTimestamp = new Date();
        this.location = new Location();
    }

    public ElementEntity(String name) {
        this();
        this.name = name;
    }

    public ElementEntity(String name, String type, Location location, Date creationTimestamp, String creatorEmail,
                         String creatorSmartspace, boolean expired, Map<String, Object> moreAtrributes) {
        this();
        this.name = name;
        this.type = type;
        this.location = location;
        this.creationTimestamp = creationTimestamp;
        this.creatorEmail = creatorEmail;
        this.creatorSmartspace = creatorSmartspace;
        this.expired = expired;
        this.moreAttributes = moreAtrributes;

    }

    public String getElementId() {
        return elementId;

    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementSmartspace() {
        return elementSmartspace;

    }

    public void setElementSmartspace(String elementSmartspace) {
        this.elementSmartspace = elementSmartspace;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getCreatorSmartspace() {
        return creatorSmartspace;
    }

    public void setCreatorSmartspace(String creatorSmartspace) {
        this.creatorSmartspace = creatorSmartspace;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }


    public Map<String, Object> getMoreAttributes() {
        return moreAttributes;
    }

    public void setMoreAttributes(Map<String, Object> moreAttributes) {
        this.moreAttributes = moreAttributes;
    }

    @Override
    public String getKey() {
        return this.elementSmartspace + "|" + this.elementId;
    }

    @Override
    public void setKey(String key) {
        String[] tempKey = key.split("\\|");
        setElementSmartspace(tempKey[0]);
        setElementId(tempKey[1]);

    }

}
