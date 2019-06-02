package a1door.monopoly.Boundaries;

import java.util.Date;
import java.util.Map;

import a1door.monopoly.Entities.ActionEntity;

public class ActionBoundary {

    private Key actionKey;
    private String type;
    private Date created;
    private Key element;
    private UserKey player;
    private Map<String, Object> properties;

    public ActionBoundary() {
        this.actionKey=new Key();
        this.element=new Key();
        this.player=new UserKey();
    }

    public ActionBoundary(ActionEntity entity) {
        this();
        String ActionId = entity.getActionId();
        String ActionSmartspace = entity.getActionSmartspace();
        if ((ActionId != null) && (ActionSmartspace != null)) {

            this.actionKey = new Key(ActionId, ActionSmartspace);

        }


        String ElementId = entity.getElementId();
        String ElementSmartspace = entity.getElementSmartspace();

        if (ElementId != null)
            this.element.setId(ElementId);

        if(ElementSmartspace != null)
            this.element.setSmartspace(ElementSmartspace);

        String userEmail = entity.getPlayerEmail();
        String userSmartSpace = entity.getPlayerSmartspace();



        if (userSmartSpace != null)
            this.player.setSmartspace(userSmartSpace);

        if (userEmail != null)
            this.player.setEmail(userEmail);

        this.type = entity.getActionType();
        this.created = entity.getCreationTimestamp();
        this.properties = entity.getMoreAttributes();
    }

    public ActionBoundary(String type, Key element, UserKey player, Map<String, Object> properties) {
        this.type = type;
        this.element = element;
        this.player = player;
        this.properties = properties;
    }

    public ActionEntity toEntity() {

        ActionEntity entity = new ActionEntity();

        if (this.actionKey != null) {
            entity.setKey(actionKey.getSmartspace() + "|" + actionKey.getId());
        }

        entity.setActionType(this.type);
        entity.setElementSmartspace(this.element.getSmartspace());
        entity.setElementId(this.element.getId());
        entity.setPlayerEmail(this.player.getEmail());
        entity.setPlayerSmartspace(this.player.getSmartspace());
        entity.setCreationTimestamp(this.created);
        entity.setMoreAttributes(this.properties);

        return entity;
    }

    public Key getActionKey() {
        return actionKey;
    }

    public void setActionKey(Key actionKey) {
        this.actionKey = actionKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Key getElement() {
        return element;
    }

    public void setElement(Key element) {
        this.element = element;
    }

    public UserKey getPlayer() {
        return player;
    }

    public void setPlayer(UserKey player) {
        this.player = player;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ActionBoundary [actionKey=" + actionKey + ", type=" + type + ", created=" + created + ", element="
                + element + ", player=" + player + ", properties=" + properties + "]";
    }
}
