package com.example.recyle.Model;

public class ModelRekreasi {
    String pId, pJudul_Rekreasi, pIsi_Rekreasii, pImage, pTime, uid, uEmail, uDp, uName;

    public ModelRekreasi() {
    }


    public ModelRekreasi(String pId, String pJudul_Rekreasi, String pIsi_Rekreasii,
                         String pImage, String pTime, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pJudul_Rekreasi = pJudul_Rekreasi;
        this.pIsi_Rekreasii = pIsi_Rekreasii;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpJudul_Rekreasi() {
        return pJudul_Rekreasi;
    }

    public void setpJudul_Rekreasi(String pJudul_Rekreasi) {
        this.pJudul_Rekreasi = pJudul_Rekreasi;
    }

    public String getpIsi_Rekreasii() {
        return pIsi_Rekreasii;
    }

    public void setpIsi_Rekreasii(String pIsi_Rekreasii) {
        this.pIsi_Rekreasii = pIsi_Rekreasii;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}