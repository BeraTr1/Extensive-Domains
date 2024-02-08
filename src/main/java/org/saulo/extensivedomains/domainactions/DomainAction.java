package org.saulo.extensivedomains.domainactions;

import java.util.List;

public enum DomainAction {

    CREATE_CURRENCY("create_currency"),
    CHANGE_NAME("change_name"),
    CREATE_TITLES("create_titles")

    ;
    private String label;

    private DomainAction(String label) {
        this.label = label;
    }

    public static DomainAction getDomainActionFromLabel(String label) {
        for (DomainAction action : DomainAction.values()) {
            boolean foundAction = action.getLabel().equals(label);

            if (!foundAction) {
                continue;
            }

            return action;
        }

        return null;
    }

    public String getLabel() {
        return this.label;
    }
}
