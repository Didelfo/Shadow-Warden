package dev.didelfo.shadowWarden.manager.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.manager.inventory.componets.Permissions;
import dev.didelfo.shadowWarden.manager.inventory.componets.User;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_HomeMenu;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_MenuRoles;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_PermissionMenu;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_PlayerMenu;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffAdd;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffList;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private ShadowWarden pluign;
    private Map<UUID, AllMenus> open_menus = new HashMap<>();
    private List<User> listaJugadores;
    private User jugadorSeleccionado;
    private int idRolSeleccionado;
    private List<Permissions> todosLosPermisos;
    private List<Permissions> permissionsSelecionados;


    public InventoryManager(ShadowWarden pl){
        this.pluign = pl;
    }


    public void openInv(Player p, AllMenus menu){
        Inventory inv = null;

        switch (menu){
            case SMS_StaffList -> {
                inv = SMS_StaffList.get();
            }
            case SMS_StaffMenu -> {
                inv = SMS_StaffMenu.get();
            }

            // Gestion de permisos de usuarios y rol
            case PERMSAPP_HomeMenu -> {
                inv = PERMSAPP_HomeMenu.get();
            }
            case PERMSAPP_MenuRoles -> {
                inv = PERMSAPP_MenuRoles.get();
            }
            case PERMSAPP_PermissionMenu -> {
                inv = PERMSAPP_PermissionMenu.get();
            }
            case PERMSAPP_PlayerMenu -> {
                inv = PERMSAPP_PlayerMenu.get();
            }


            default -> {}
        }

        if (inv != null) {
            open_menus.put(p.getUniqueId(), menu);
            p.openInventory(inv);
        }

    }

    public void closeInv(Player p){
        if (open_menus.containsKey(p.getUniqueId())){
            open_menus.remove(p.getUniqueId());
            p.closeInventory();
        }
    }

    public Map<UUID, AllMenus> getOpen_menus() {return open_menus; }

    public void cargarTodosPermisos(){
        pluign.getExecutor().execute(() -> {
            EncryptedDatabase dbE = new EncryptedDatabase(pluign);
            dbE.connect();
            todosLosPermisos = dbE.getAllPermissions();
            dbE.close();
        });
    }

    public void limpiarTodosPermisos(){
        todosLosPermisos = null;
    }

    public void cargarTodosJugadores(){
            EncryptedDatabase dbE = new EncryptedDatabase(pluign);
            dbE.connect();
            listaJugadores = dbE.getAllUser();
            dbE.close();
    }
    public void limpiarTodosUsers(){
        listaJugadores = null;
    }


    public void setOpen_menus(Map<UUID, AllMenus> open_menus) {
        this.open_menus = open_menus;
    }

    public List<User> getListaJugadores() {
        return listaJugadores;
    }

    public void setListaJugadores(List<User> listaJugadores) {
        this.listaJugadores = listaJugadores;
    }

    public User getJugadorSeleccionado() {
        return jugadorSeleccionado;
    }

    public void setJugadorSeleccionado(User jugadorSeleccionado) {
        this.jugadorSeleccionado = jugadorSeleccionado;
    }

    public int getIdRolSeleccionado() {
        return idRolSeleccionado;
    }

    public void setIdRolSeleccionado(int idRolSeleccionado) {
        this.idRolSeleccionado = idRolSeleccionado;
    }

    public List<Permissions> getTodosLosPermisos() {
        return todosLosPermisos;
    }

    public void setTodosLosPermisos(List<Permissions> todosLosPermisos) {
        this.todosLosPermisos = todosLosPermisos;
    }

    public List<Permissions> getPermissionsSelecionados() {
        return permissionsSelecionados;
    }

    public void setPermissionsSelecionados(List<Permissions> permissionsSelecionados) {
        this.permissionsSelecionados = permissionsSelecionados;
    }
}
