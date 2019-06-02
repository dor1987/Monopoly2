package a1door.monopoly.Boundaries;

import java.util.Date;
import java.util.Map;

import a1door.monopoly.Entities.ElementEntity;
import a1door.monopoly.Entities.Location;

public class ElementBoundary {

    private Key key;
    private String elementType;
    private String name;
    private boolean expired;
    private Date created;
    private UserKey creator;
    private BoundaryLocation latlng;
    private Map<String, Object> elementProperties;

    public ElementBoundary() {

    }

    public ElementBoundary(String elementType, String name, boolean expired, UserKey creator,
                           BoundaryLocation latlng, Map<String, Object> elementProperties) {
        this.elementType = elementType;
        this.name = name;
        this.expired = expired;
        this.creator = creator;
        this.latlng = latlng;
        this.elementProperties = elementProperties;

    }

    public ElementBoundary(ElementEntity elementEntity) {
        if (elementEntity.getKey() != null && !elementEntity.getKey().isEmpty())
            this.key = new Key(elementEntity.getElementId() + "", elementEntity.getElementSmartspace());
        else
            this.key = null;

        this.elementType = elementEntity.getType();
        this.name = elementEntity.getName();
        this.expired = elementEntity.isExpired();
        this.created = elementEntity.getCreationTimestamp();
        this.creator = new UserKey(elementEntity.getCreatorSmartspace(), elementEntity.getCreatorEmail());

        if (elementEntity.getLocation() != null)
            this.latlng = new BoundaryLocation(elementEntity.getLocation().getX(), elementEntity.getLocation().getY());
        else
            this.latlng = null;

        this.elementProperties = elementEntity.getMoreAttributes();

    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserKey getCreator() {
        return creator;
    }

    public void setCreator(UserKey creator) {
        this.creator = creator;
    }

    public BoundaryLocation getLatlng() {
        return latlng;
    }

    public void setLatlng(BoundaryLocation latlng) {
        this.latlng = latlng;
    }

    public Map<String, Object> getElementProperties() {
        return elementProperties;
    }

    public void setElementProperties(Map<String, Object> elementProperties) {
        this.elementProperties = elementProperties;
    }

    public ElementEntity toEntity() {
        ElementEntity elementEntity = new ElementEntity();

        if(this.key != null && this.key.getId() != null && this.key.getSmartspace() != null) {
            elementEntity.setKey(this.key.getSmartspace() + "|" + this.key.getId());

        }


        elementEntity.setType(this.elementType);
        elementEntity.setName(this.name);
        elementEntity.setExpired(this.expired);
        elementEntity.setCreationTimestamp(this.created);

        if (this.creator != null) {
            elementEntity.setCreatorEmail(this.creator.getEmail());
            elementEntity.setCreatorSmartspace(this.creator.getSmartspace());
        }

        elementEntity.setLocation(new Location(this.latlng.getLat(), this.latlng.getLng()));
        elementEntity.setMoreAttributes(this.elementProperties);

        return elementEntity;
    }

    @Override
    public String toString() {
        return "ElementBoundary [key=" + key + ", elementType=" + elementType + ", name=" + name + ", expired="
                + expired + ", created=" + created + ", creator=" + creator + ", latlng=" + latlng
                + ", elementProperties=" + elementProperties + "]";
    }


}
