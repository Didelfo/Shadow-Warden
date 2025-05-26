package dev.didelfo.shadowWarden.manager.inventory.componets;

import org.bukkit.inventory.ItemStack;

public class User {
    private String uuidMojan;
    private String name;
    private ItemStack head;
    private int idRol;


    public User(String uuidMojan, String name, ItemStack head, int idRol) {
        this.uuidMojan = uuidMojan;
        this.name = name;
        this.head = head;
        this.idRol = idRol;
    }

    public String getUuidMojan() {
        return uuidMojan;
    }

    public void setUuidMojan(String uuidMojan) {
        this.uuidMojan = uuidMojan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getHead() {
        return head;
    }

    public void setHead(ItemStack head) {
        this.head = head;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
}
