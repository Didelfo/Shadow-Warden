package dev.didelfo.shadowWarden.models;

public class Sanction {
    private String expire;
    private String reason;
    private String staffName;

    public Sanction() {
        this.expire = "";
        this.reason = "";
        this.staffName = "";
    }


    public Sanction(String expire, String reason, String staffName) {
        this.expire = expire;
        this.reason = reason;
        this.staffName = staffName;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
