package org.saulo.extensivedomains.objects;

import org.bukkit.entity.Player;
import org.saulo.extensivedomains.playerconditions.Condition;

import java.util.*;
import java.util.function.Predicate;

public class ClaimPermission {
    public enum ClaimAction {

        DESTROY("destroy"),
        BUILD("build");

        private String name;

        private ClaimAction(String name) {
            this.name = name;
        }

        public static ClaimAction getClaimActionByName(String name) {
            for (ClaimAction claimAction : ClaimAction.values()) {
                if (!claimAction.getName().equalsIgnoreCase(name)) continue;

                return claimAction;
            }

            return null;
        }

        private String getName() {
            return this.name;
        }
    }

    private Map<ClaimAction, List<Condition>> permissionConditionsMap = new HashMap<>();

    public void addPermissionCondition(ClaimAction claimAction, Condition condition) {
        List<Condition> conditions = this.permissionConditionsMap.get(claimAction);

        if (conditions == null) {
            conditions = new ArrayList<>();
        }

        if (conditions.contains(condition)) {
            return;
        }

        conditions.add(condition);
        this.permissionConditionsMap.put(claimAction, conditions);
    }

    public void removePermissionCondition(ClaimAction claimAction, Condition condition) {
        List<Condition> conditions = this.permissionConditionsMap.get(claimAction);

        if (conditions == null) {
            conditions = new ArrayList<>();
        }

        if (!conditions.contains(condition)) {
            return;
        }

        conditions.remove(condition);
        this.permissionConditionsMap.put(claimAction, conditions);
    }

    public boolean playerHasPermission(Player player, ClaimAction claimAction) {
        List<Condition> conditions = this.permissionConditionsMap.get(claimAction);

        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        for (Predicate<Player> condition : conditions) {
            if (condition.test(player)) {
                return true;
            }
        }

        return false;
    }
}
