package dev.didelfo.shadowWarden.manager.inventory.componets;

public class Permissions {
    private int id;
    private String name;
    private String description;
    private Boolean select;

    public Permissions(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.select = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }
}
