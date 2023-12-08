package lol.maltest.islandsmp.entities.sub;

import java.util.HashSet;
import java.util.Set;

public class IslandPermissions {
    private Set<String> memberPermissions = new HashSet<>();
    private Set<String> moderatorPermissions = new HashSet<>();
    private Set<String> administratorPermissions = new HashSet<>();
    private Set<String> ownerPermissions = new HashSet<>();

    // Getters and Setters

    // Add permissions methods

    public void addMemberPermission(String permission) {
        this.memberPermissions.add(permission);
    }

    public void addModeratorPermission(String permission) {
        this.moderatorPermissions.add(permission);
    }

    public void addAdministratorPermission(String permission) {
        this.administratorPermissions.add(permission);
    }


    // Check permissions methods

    public boolean hasMemberPermission(String permission) {
        return this.memberPermissions.contains(permission);
    }

    public boolean hasModeratorPermission(String permission) {
        return this.moderatorPermissions.contains(permission);
    }

    public boolean hasAdministratorPermission(String permission) {
        return this.administratorPermissions.contains(permission);
    }



    // Additional methods for removing and other operations can be added as needed.
}