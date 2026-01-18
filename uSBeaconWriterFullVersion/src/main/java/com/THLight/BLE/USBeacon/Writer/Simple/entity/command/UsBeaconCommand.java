package com.THLight.BLE.USBeacon.Writer.Simple.entity.command;

public class UsBeaconCommand {
    public static final int BUF_LEN = 18;

    /**
     * system commands.
     */
    public static final byte CMD_FACTORY_RESET = 0x01;
    /**
     * 0x01, factory reset and reboot usbeacon, cmd(rule + 0x01)
     */
    public static final byte CMD_REBOOT = 0x02;
    /**
     * 0x02, reboot, cmd(rule + 0x02)
     */
    public static final byte CMD_CHECK_ACC_UUID = 0x03;
    /**
     * 0x03, authorization, cmd(rule + 0x03 + acc_uuid)
     */
    public static final byte CMD_SET_ACC_UUID = 0x04;
    /**
     * 0x04, 18 bytes, cmd(rule + 0x04 + acc_uuid)
     */
    public static final byte CMD_GET_MAC_ADDR = 0x05;
    /**
     * 0x05, cmd(rule + 0x05), response 8 bytes, data(rule + 0x05 + mac_addr)
     */
//	public static final byte CMD_GET_FW_VER					= 0x06;				/** 0x06, cmd(rule + 0x06), response 4 bytes, data(rule + 0x06 + FW_VER --> Big-Endian) */
    public static final byte CMD_GET_INFO = 0x06;
    /**
     * 0x06, cmd(rule + 0x06), response 7 bytes, data(rule + 0x06 + VER_MAJOR + VER_MINOR + type + Battery_MINOR + Battery_MAJOR)
     */
    public static final byte CMD_GET_NAME = 0x07;
    public static final byte CMD_SET_NAME = 0x08;
    public static final byte CMD_SET_MAC_ADDR = 0x09;

    /**
     * advertise data.
     */
    public static final byte CMD_R_BEACON_UUID = 0x10;
    /**
     * 0x10, response 18 bytes, data(rule + 0x10 + beacon_uuid).
     */
    public static final byte CMD_R_BEACON_MAJOR = 0x11;
    /**
     * 0x11, response 4 bytes, data(rule + 0x11 + MAJOR).
     */
    public static final byte CMD_R_BEACON_MINOR = 0x12;
    /**
     * 0x12, response 4 bytes, data(rule + 0x12 + MINOR).
     */
    public static final byte CMD_R_BEACON_RSSI = 0x13;                /** 0x13, response 3 bytes, data(rule + 0x13 + RSSI). */

    /**
     * request information, command only.
     */
    public static final byte CMD_R_ADVERTISE_PER_SECOND = 0x14;
    /**
     * 0x14, response 3 bytes, data(rule + 0x14 + ADVERTISE_PER_SECOND).
     */
    public static final byte CMD_R_ADVERTISE_DELAY = 0x15;
    /**
     * 0x15, response 3 bytes, data(rule + 0x15 + TIME_BEFORE_ENTER_BEACON).
     */
    public static final byte CMD_R_TIME_BEFORE_ENTER_BEACON = 0x15;
    public static final byte CMD_R_TX_POWER = 0x16;
    /**
     * 0x16, response 3 bytes, data(rule + 0x16 + tx_power).
     */
    public static final byte CMD_R_BAT_UUID = 0x17;

    //beacon 2640
    public static final byte CMD_R_SYNC_WORK_ADJUST_TIME = 0x18;

    //wearable beacon
    public static final byte CMD_R_MOVE_STATUS = 0x1B;                /** 0x1B, 2 bytes, cmd(rule + 0x1B + move_status).*/

    /**
     * advertise data.
     */
    public static final byte CMD_W_BEACON_UUID = 0x20;
    /**
     * 0x20, 18 bytes, cmd(rule + 0x20 + beacon_uuid)
     */
    public static final byte CMD_W_BEACON_MAJOR = 0x21;
    /**
     * 0x21, 4 bytes, cmd(rule + 0x21 + Major --> Big-Endian)
     */
    public static final byte CMD_W_BEACON_MINOR = 0x22;
    /**
     * 0x22, 4 bytes, cmd(rule + 0x22 + Minor --> Big-Endian)
     */
    public static final byte CMD_W_BEACON_RSSI = 0x23;                /** 0x23, 3 bytes, cmd(rule + 0x23 + Rssi) */

    /**
     * write information.
     */
    public static final byte CMD_W_ADVERTISE_PER_SECOND = 0x24;
    /**
     * 0x24, 3 bytes, cmd(rule + 0x24 + count_per_second)
     */
    public static final byte CMD_W_IB_FREQ_DELAY = 0x24;
    //public static final byte CMD_W_ADVERTISE_DELAY			= 0x25;				/** 0x25, 3 bytes, cmd(rule + 0x25 + time_before_enter_beacon) */
    public static final byte CMD_W_TIME_BEFORE_ENTER_BEACON = 0x25;
    public static final byte CMD_W_TX_POWER = 0x26;
    /**
     * 0x26, 3 bytes, cmd(rule + 0x26 + tx_power)
     */
    public static final byte CMD_W_BAT_UUID = 0x27;
    /**
     * write information.
     */
    public static final byte CMD_W_SYNC_TIME = 0x28;
    /**
     * 0x28, 6 bytes, cmd(rule + 0x28 + Time(4).
     */
    public static final byte CMD_W_SYNC_WORK_ADJUST_TIME = 0x28;

    public static final byte CMD_W_MAJOR_MINOR = 0x29;
    /**
     * 0x29, 6 bytes, cmd(rule + 0x29 + Major(2) + Minor(2).
     */
    public static final byte CMD_W_TX_POWER_RSSI = 0x2A;
    /**
     * 0x2A, 4 bytes, cmd(rule + 0x2A + Tx + Rssi_on_one_meter).
     */
    public static final byte CMD_W_CLR_MOVE_STATUS = 0x2B;
    /**
     * 0x2B, 2 bytes, cmd(rule + 0x2B).
     */
    public static final byte CMD_W_SPECIFIC_DATA = 0x2C;                /** 0x2C, 11 bytes, cmd(rule + 0x2C + data(9)).*/

    /********2.1.3 Wearable cmd********/
    public static final byte CMD_W_SECONDS_2_REST = 0x2B;
    /**
     * 0x2B, 4 bytes, cmd(rule + 0x2B + time).
     */
    public static final byte CMD_R_SECONDS_2_REST = 0x2C;                /** 0x2C, 2 bytes, cmd(rule + 0x2C).*/
    /**********************************/
    public static final byte CMD_W_BUZZER = 0x2D;                /** 0x2D, 9 bytes, cmd(rule + 0x2D + frequency(2) + on_ms(2) + off_ms(2) + count(1)), ack = ACK_SUCCESS / ERR_ERROR **/

    public static final byte CMD_GET_BEACON_TYPE = 0x2E;
    /**
     * admin commands.
     */
    public static final byte CMD_ADMIN_FACTORY_RESET = 0x30;
    /**
     * 0x30, 18 bytes, cmd(rule + 0x30 + admin_key)
     */
    public static final byte CMD_ADMIN_ENTER_USBEACON = 0x31;
    /**
     * 0x31, 18 bytes, cmd(rule + 0x31 + admin_key)
     */
    public static final byte CMD_ADMIN_LOGIN = 0x32;
    /**
     * 0x32, 18 bytes, cmd(rule + 0x32 + admin_key)
     */
    public static final byte CMD_ADMIN_ENTER_IBEACON = 0x33;                /** 0x33, 8 bytes, cmd(rule + 0x33 + ms + major + minor) --> Big-Endian */

    /********2.1.3 Wearable cmd********/
    public static final byte CMD_R_STEPS = 0x31;
    public static final byte CMD_W_ENABLE_SENSOR_DATA = 0x35;
    public static final byte CMD_R_ENABLE_SENSOR_DATA = 0x36;
    /**
     * 0x32, 4 bytes, cmd(rule + 0x36 + frequency)
     */
    public static final byte CMD_W_REST_FREQ = 0x37;
    /**
     * 0x32, 2 bytes, cmd(rule + 0x37)
     */
    public static final byte CMD_R_REST_FREQ = 0x38;
    public static final byte CMD_W_ENABLE_ADV_STEPS = 0x3B;
    public static final byte CMD_R_ENABLE_ADV_STEPS = 0x3C;
    public static final byte CMD_W_ENABLE_ALARM = 0x3D;
    public static final byte CMD_R_ENABLE_ALARM = 0x3E;
    /********2.2.0 Wearable cmd********/
    public static final byte CMD_W_DISABLE_ALARM_SIGNALS = 0x41;
    public static final byte CMD_R_DISABLE_ALARM_SIGNALS = 0x42;
    /********2.2.1 Wearable cmd********/
    public static final byte CMD_W_GSENSOR_SETTING = 0x39;
    public static final byte CMD_R_GSENSOR_SETTING = 0x3A;
    /*************Line CMD*********************/
    public static final byte CMD_R_LINE_INFO = 0x47;
    public static final byte CMD_W_LINE_INFO = 0x48;

    /*************ITRI CMD*********************/
    public static final byte CMD_R_SAVE_DATA_COUNT = 0x50;
    public static final byte CMD_R_SAVE_DATA = 0x51;
    public static final byte CMD_R_RESET_SAVE_DATA_COUNT = 0x52;

    /*************TBW(儲位燈) CMD*********************/
    public static final byte CMD_W_REMOTE_ID = 0x60;
    public static final byte CMD_R_REMOTE_ID = 0x61;
    public static final byte CMD_W_ADV_INFO = 0x62;
    public static final byte CMD_R_ADV_INFO = 0x63;
    /*************加密 CMD*********************/
    public static final byte CMD_R_SIGNIN_USERID = 0x67;
    public static final byte CMD_W_SIGNIN_USERID = 0x68;
    public static final byte CMD_R_SIGNIN_HWID = 0x69;
    public static final byte CMD_W_SIGNIN_HWID = 0x6A;
    public static final byte CMD_R_SIGNIN_AESKEY = 0x6B;
    public static final byte CMD_W_SIGNIN_AESKEY = 0x6C;

    /*************Atmosic CMD*********************/
    public static final byte CMD_R_ENABLE_LOW_BATTERY_LED = 0x70;
    public static final byte CMD_W_ENABLE_LOW_BATTERY_LED = 0x71;

    /**
     * error.
     */
    public static final byte ACK_SUCCESS = (byte) 0x80;
    /**
     * 0x80, 2 bytes. ack(rule, 0x80). command successfully.
     */
    public static final byte ERR_ERROR = (byte) 0x81;
    /**
     * 0x81, 2 bytes. ack(rule, 0x81). all other errors which are not list follow.
     */
    public static final byte ERR_ACC_UUID = (byte) 0x82;
    /**
     * 0x82, 2 bytes. ack(rule, 0x82). set account uuid but it not a valid one.
     */
    public static final byte ERR_AUTH = (byte) 0x83;
    /**
     * 0x83, 2 bytes. ack(rule, 0x83). not authentication yet.
     */
    public static final byte ERR_BEACON_UUID = (byte) 0x84;
    /**
     * 0x84, 2 bytes. ack(rule, 0x84). set beacon uuid but it not a valid one.
     */
    public static final byte ERR_ACC_FREE = (byte) 0x85;
    /**
     * 0x85, 2 bytes. ack(rule, 0x85). UUID in USBeacon is empty, any UUID is acceptable to write.
     */

    public static final byte ERR_ACC_NOT_MATCH = (byte) 0x86;
    /**
     * 0x86, 2 bytes. ack(rule, 0x86). USBeacon's and App's uuid is different.
     */
    public static final byte ERR_SYNTAX = (byte) 0x87;
    public static final byte ERR_CMD_NOT_FOUND = (byte) 0x88;
    /** ================================================ */
    /**
     * AirLocate 1 : E2C56DB5-DFFB-48D2-B060-D0F5A71096E0
     */
    public static final byte[] DEF_UUID_RAW = {
            (byte) 0xE2, (byte) 0xC5, (byte) 0x6D, (byte) 0xB5,
            (byte) 0xDF, (byte) 0xFB, (byte) 0x48, (byte) 0xD2,
            (byte) 0xB0, (byte) 0x60, (byte) 0xD0, (byte) 0xF5,
            (byte) 0xA7, (byte) 0x10, (byte) 0x96, (byte) 0xE0,
    };

    /** ================================================ */
    /**
     * newData= {rule, cmd, data[]}.
     */
    public static byte[] genCmdData(byte cmd, byte[] data) {
        byte[] newData = new byte[BUF_LEN];
        java.util.Random rand = new java.util.Random();
        rand.nextBytes(newData);
        newData[1] = cmd;
        System.arraycopy(data, 0, newData, 2, data.length);
        return newData;
    }

    /** ================================================ */
    /**
     * newData= {rule, cmd, data}.
     */
    public static byte[] genCmdData(byte cmd, byte data) {
        byte[] newData = new byte[BUF_LEN];
        java.util.Random rand = new java.util.Random();
        rand.nextBytes(newData);
        newData[1] = cmd;
        newData[2] = data;
        return newData;
    }

    /** ================================================ */
    /**
     * newData= {rule, cmd, data}.
     */
    public static byte[] genCmdData(byte cmd) {
        byte[] newData = new byte[BUF_LEN];
        java.util.Random rand = new java.util.Random();
        rand.nextBytes(newData);
        newData[1] = cmd;
        return newData;
    }
}