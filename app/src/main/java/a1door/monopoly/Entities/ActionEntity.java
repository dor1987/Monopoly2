package a1door.monopoly.Entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActionEntity implements SmartspaceEntity<String>{
        private String actionSmartspace;
        private String actionId;
        private String elementSmartspace;
        private String elementId;
        private String playerSmartspace;
        private String playerEmail;
        private String actionType;
        private Date creationTimestamp;
        private Map<String,Object> moreAttributes;


        public ActionEntity() {
            //this.creationTimestamp = new Date();
            this.moreAttributes = new HashMap<>();
        }



        public ActionEntity(String actionType) {
            this();
            this.actionType = actionType;
        }


        public ActionEntity(String elementSmartspace, String elementId,
                            String playerSmartspace, String playerEmail, String actionType, Date creationTimestamp,
                            Map<String, Object> moreAttributes) {
            this();
            this.elementSmartspace = elementSmartspace;
            this.elementId = elementId;
            this.playerSmartspace = playerSmartspace;
            this.playerEmail = playerEmail;
            this.actionType = actionType;
            this.creationTimestamp = creationTimestamp;
            this.moreAttributes = moreAttributes;
        }


        public String getActionSmartspace() {
            return actionSmartspace;
        }

        public void setActionSmartspace(String actionSmartspace) {
            this.actionSmartspace = actionSmartspace;
        }

        public String getActionId() {
            return actionId;
        }

        public void setActionId(String actionId) {
            this.actionId = actionId;
        }

        public String getElementSmartspace() {
            return this.elementSmartspace;

        }

        public void setElementSmartspace(String elementSmartspace) {
            this.elementSmartspace = elementSmartspace;
        }

        public String getPlayerSmartspace() {
            return playerSmartspace;
        }

        public void setPlayerSmartspace(String playerSmartspace) {
            this.playerSmartspace = playerSmartspace;
        }

        public String getPlayerEmail() {
            return playerEmail;
        }

        public void setPlayerEmail(String playerEmail) {
            this.playerEmail = playerEmail;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public Date getCreationTimestamp() {
            return creationTimestamp;
        }

        public void setCreationTimestamp(Date creationTimestamp) {
            this.creationTimestamp = creationTimestamp;
        }

        public Map<String, Object> getMoreAttributes() {
            return moreAttributes;
        }

        public void setMoreAttributes(Map<String, Object> moreAttributes) {
            this.moreAttributes = moreAttributes;
        }

        @Override
        public String getKey() {
            return this.actionSmartspace+"|"+this.actionId;
        }

        @Override
        public void setKey(String key) {
            String[] tempKey = key.split("\\|");
            setActionSmartspace(tempKey[0]);
            setActionId(tempKey[1]);

        }

        public String getElementId() {
            return elementId;
        }

        public void setElementId(String elementId) {
            this.elementId = elementId;
        }

    }

