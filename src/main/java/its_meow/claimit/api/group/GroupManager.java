package its_meow.claimit.api.group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.GroupClaimAddedEvent;
import its_meow.claimit.api.serialization.GlobalDataSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GroupManager {
    
    private static HashMap<String, Group> groups = new HashMap<String, Group>();
    private static HashMap<ClaimArea, Set<Group>> claimToGroup = new HashMap<ClaimArea, Set<Group>>();

    public static boolean addGroup(Group group) {
        if(groups.containsKey(group.getName())) {
            return false;
        }
        groups.put(group.getName(), group);
        return true;
    }

    public static void removeGroup(Group group) {
        groups.remove(group.getName());
    }

    @Nullable
    public static Group getGroup(String name) {
        return groups.get(name);
    }
    
    @Nullable
    public static ImmutableSet<Group> getGroupsForClaim(ClaimArea claim) {
        if(!claimToGroup.containsKey(claim)) {
            return null;
        }
        return ImmutableSet.copyOf(claimToGroup.get(claim));
    }

    public static boolean renameGroup(String name, String newName) {
        if(!groups.containsKey(name) || groups.containsKey(newName)) {
            return false;
        }
        Group group = groups.get(name);
        groups.remove(name);
        groups.put(newName, group);
        group.name = newName;
        return true;
    }

    public static ImmutableSet<Group> getGroups() {
        return ImmutableSet.copyOf(groups.values());
    }
    
    public static void serialize() {
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        NBTTagCompound groupsTag = new NBTTagCompound();
        for(String groupName : groups.keySet()) {
            NBTTagCompound groupCompound = groups.get(groupName).serialize();
            groupsTag.setTag(groupName, groupCompound);
        }
        comp.setTag("GROUPS", groupsTag);
        store.markDirty();
    }
    
    public static void deserialize() {
        groups.clear();
        GlobalDataSerializer store = GlobalDataSerializer.get();
        NBTTagCompound comp = store.data;
        if(comp != null) {
            NBTTagCompound groupsTag = comp.getCompoundTag("GROUPS");
            for(String key : groupsTag.getKeySet()) {
                System.out.println("Loading " + key);
                Group group = Group.deserialize(groupsTag.getCompoundTag(key));
                if(!addGroup(group)) {
                    ClaimItAPI.logger.error("Duplicate group name of " + group.name + " failed to load! Was the data edited?");
                }
            }
        }
    }
    
    @Mod.EventBusSubscriber(modid = ClaimItAPI.MOD_ID)
    private static class InternalGroupEventHandler {

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGroupClaimAdded(GroupClaimAddedEvent e) {
            claimToGroup.putIfAbsent(e.getClaim(), new HashSet<Group>());
            claimToGroup.get(e.getClaim()).add(e.getGroup());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onGroupClaimRemoved(GroupClaimAddedEvent e) {
            if(claimToGroup.putIfAbsent(e.getClaim(), new HashSet<Group>()) != null)
                claimToGroup.get(e.getClaim()).remove(e.getGroup());
        }

    }

}