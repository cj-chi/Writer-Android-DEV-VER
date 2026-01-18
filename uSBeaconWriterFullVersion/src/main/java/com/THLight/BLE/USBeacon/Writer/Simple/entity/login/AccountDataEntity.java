package com.THLight.BLE.USBeacon.Writer.Simple.entity.login;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccountDataEntity implements Serializable {

    @SerializedName("account_uuid")
    private String accessUUID;

    @SerializedName("beacon_uuid")
    private String beaconUUID;

    @SerializedName("dataquery_uuid")
    private String queryUUID;

    @SerializedName("mbr_id")
    private String memberId;

    @SerializedName("status")
    private String status;

    public String getAccessUUID() {
        return accessUUID;
    }

    public void setAccessUUID(String accessUUID) {
        this.accessUUID = accessUUID;
    }

    public String getBeaconUUID() {
        return beaconUUID;
    }

    public void setBeaconUUID(String beaconUUID) {
        this.beaconUUID = beaconUUID;
    }

    public String getQueryUUID() {
        return queryUUID;
    }

    public void setQueryUUID(String queryUUID) {
        this.queryUUID = queryUUID;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] generateAccessUuid() { // 產生並返回需與裝置權限比對的UUID
        return generateRealUuid(accessUUID);
    }

    public byte[] generateBeaconUuid() { // 產生寫進Beacon 的 UUID  (Advertising)
        return generateRealUuid(beaconUUID);
    }

    private byte[] generateRealUuid(String uuid) {
        byte[] BYTES_UUID = new byte[16];
        String[] STRING_UUID = new String[16];
        if (uuid.length() == 36) {
            int start = 0;
            int end;
            for (int i = 0; i < 16; i++) {
                end = start + 2;
                STRING_UUID[i] = uuid.substring(start, end);
                start = (i == 3 || i == 5 || i == 7 || i == 9) ? end + 1 : end;
                BYTES_UUID[i] = (byte) (Integer.parseInt(STRING_UUID[i], 16) & 0x00FF);
            }
        }
        return BYTES_UUID;
    }
}
