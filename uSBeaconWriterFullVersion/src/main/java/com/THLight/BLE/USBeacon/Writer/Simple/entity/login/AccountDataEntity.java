package com.THLight.BLE.USBeacon.Writer.Simple.entity.login;

import com.THLight.BLE.USBeacon.Writer.Simple.util.BytesUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.google.gson.annotations.SerializedName;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.Serializable;
import java.util.UUID;

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

    public static AccountDataEntity fromCredentials(String account, String password) {
        AccountDataEntity entity = new AccountDataEntity();
        entity.accessUUID = createAccessUuid(account, password);
        entity.beaconUUID = createBeaconUuid(account, password);
        entity.queryUUID = createQueryUuid(account, password);
        entity.memberId = "";
        entity.status = "";
        return entity;
    }

    public static String createAccessUuid(String account, String password) {
        return generateAccessUuidFromCredentialsRepeat(account, password);
    }

    public static String createBeaconUuid(String account, String password) {
        return generateUuidFromCredentials(account, password, "beacon");
    }

    public static String createQueryUuid(String account, String password) {
        return generateUuidFromCredentials(account, password, "query");
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
        if (!StringUtil.isEmpty(uuid) && uuid.length() == 36) {
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

    public static byte[] parseUuidBytes(String uuidString) {
        if (StringUtil.isEmpty(uuidString)) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(uuidString.trim());
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            return buffer.array();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String generateUuidFromCredentials(String account, String password, String purpose) {
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(password) || StringUtil.isEmpty(purpose)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = account + ":" + password + ":" + purpose;
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            byte[] uuidBytes = new byte[16];
            System.arraycopy(hash, 0, uuidBytes, 0, 16);
            uuidBytes[6] = (byte) ((uuidBytes[6] & 0x0F) | 0x40);
            uuidBytes[8] = (byte) ((uuidBytes[8] & 0x3F) | 0x80);
            return formatUuid(uuidBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String generateAccessUuidFromCredentialsRepeat(String account, String password) {
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(password)) {
            return "";
        }
        byte[] sourceBytes = (account + password).getBytes(StandardCharsets.UTF_8);
        if (sourceBytes.length == 0) {
            return "";
        }
        byte[] uuidBytes = new byte[16];
        for (int i = 0; i < uuidBytes.length; i++) {
            uuidBytes[i] = sourceBytes[i % sourceBytes.length];
        }
        return formatUuid(uuidBytes);
    }

    private static String formatUuid(byte[] bytes) {
        String hex = BytesUtil.getHexString(bytes);
        return hex.substring(0, 8) + "-"
                + hex.substring(8, 12) + "-"
                + hex.substring(12, 16) + "-"
                + hex.substring(16, 20) + "-"
                + hex.substring(20, 32);
    }
}
