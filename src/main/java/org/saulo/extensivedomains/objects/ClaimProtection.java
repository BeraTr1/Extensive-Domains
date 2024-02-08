package org.saulo.extensivedomains.objects;

public enum ClaimProtection {
    LIQUID_FLOW("liquid_flow");

    private String name;

    private ClaimProtection(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ClaimProtection getProtectionFromName(String protectionName) {
        for (ClaimProtection claimProtection : ClaimProtection.values()) {
            if (!claimProtection.getName().equalsIgnoreCase(protectionName)) continue;

            return claimProtection;
        }

        return null;
    }
}
