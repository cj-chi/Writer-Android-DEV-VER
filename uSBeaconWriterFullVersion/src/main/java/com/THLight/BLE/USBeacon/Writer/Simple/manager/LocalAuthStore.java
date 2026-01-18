package com.THLight.BLE.USBeacon.Writer.Simple.manager;

import android.content.Context;

import com.THLight.BLE.USBeacon.Writer.Simple.util.BytesUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.GsonUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class LocalAuthStore {
    private static final String STORE_FILE_NAME = "local_auth_store.json";
    private static final int SCHEMA_VERSION = 1;
    private static LocalAuthStore instance;
    private final Object lock = new Object();
    private final Context context;

    private LocalAuthStore(Context context) {
        this.context = context;
    }

    public static LocalAuthStore getInstance() {
        if (instance == null) {
            instance = new LocalAuthStore(ApplicationManager.getInstance().getContext());
        }
        return instance;
    }

    public boolean register(String account, String password) {
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(password)) {
            return false;
        }
        synchronized (lock) {
            AuthStore store = loadStore();
            if (findUser(store, account) != null) {
                return false;
            }
            long now = System.currentTimeMillis();
            UserRecord user = new UserRecord();
            user.account = account;
            user.salt = generateSaltHex();
            user.passwordPlain = password;
            user.passwordHash = hashPassword(password, user.salt);
            user.createdAt = now;
            user.updatedAt = now;
            store.users.add(user);
            saveStore(store);
            return true;
        }
    }

    public boolean verify(String account, String password) {
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(password)) {
            return false;
        }
        synchronized (lock) {
            AuthStore store = loadStore();
            UserRecord user = findUser(store, account);
            if (user == null) {
                return false;
            }
            if (!StringUtil.isEmpty(user.passwordPlain)) {
                return password.equals(user.passwordPlain);
            }
            String passwordHash = hashPassword(password, user.salt);
            boolean matched = !StringUtil.isEmpty(passwordHash) && passwordHash.equalsIgnoreCase(user.passwordHash);
            if (matched) {
                user.passwordPlain = password;
                user.updatedAt = System.currentTimeMillis();
                saveStore(store);
            }
            return matched;
        }
    }

    public boolean resetPassword(String account, String newPassword) {
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(newPassword)) {
            return false;
        }
        synchronized (lock) {
            AuthStore store = loadStore();
            UserRecord user = findUser(store, account);
            if (user == null) {
                return false;
            }
            user.salt = generateSaltHex();
            user.passwordPlain = newPassword;
            user.passwordHash = hashPassword(newPassword, user.salt);
            user.updatedAt = System.currentTimeMillis();
            saveStore(store);
            return true;
        }
    }

    public UserRecord getUser(String account) {
        if (StringUtil.isEmpty(account)) {
            return null;
        }
        synchronized (lock) {
            return findUser(loadStore(), account);
        }
    }

    private UserRecord findUser(AuthStore store, String account) {
        if (store == null || store.users == null || StringUtil.isEmpty(account)) {
            return null;
        }
        for (UserRecord user : store.users) {
            if (account.equalsIgnoreCase(user.account)) {
                return user;
            }
        }
        return null;
    }

    private AuthStore loadStore() {
        File storeFile = getStoreFile();
        if (storeFile == null || !storeFile.exists()) {
            return createEmptyStore();
        }
        String json = readFileString(storeFile);
        if (StringUtil.isEmpty(json)) {
            return createEmptyStore();
        }
        AuthStore store = GsonUtil.generateGenericData(json, AuthStore.class);
        if (store == null) {
            return createEmptyStore();
        }
        if (store.users == null) {
            store.users = new ArrayList<>();
        }
        if (store.schemaVersion <= 0) {
            store.schemaVersion = SCHEMA_VERSION;
        }
        return store;
    }

    private void saveStore(AuthStore store) {
        if (store == null) {
            return;
        }
        store.schemaVersion = SCHEMA_VERSION;
        store.updatedAt = System.currentTimeMillis();
        String json = GsonUtil.toJson(store);
        if (StringUtil.isEmpty(json)) {
            return;
        }
        File storeFile = getStoreFile();
        if (storeFile == null) {
            return;
        }
        writeFileString(storeFile, json);
    }

    private AuthStore createEmptyStore() {
        AuthStore store = new AuthStore();
        store.schemaVersion = SCHEMA_VERSION;
        store.users = new ArrayList<>();
        store.updatedAt = System.currentTimeMillis();
        return store;
    }

    private File getStoreFile() {
        if (context == null || context.getFilesDir() == null) {
            return null;
        }
        return new File(context.getFilesDir(), STORE_FILE_NAME);
    }

    private String generateSaltHex() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return BytesUtil.getHexString(salt);
    }

    private String hashPassword(String password, String saltHex) {
        if (StringUtil.isEmpty(password) || StringUtil.isEmpty(saltHex)) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = BytesUtil.hexStringToByteArray(saltHex);
            digest.update(saltBytes);
            digest.update(password.getBytes(StandardCharsets.UTF_8));
            return BytesUtil.getHexString(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readFileString(File file) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void writeFileString(File file, String content) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file, false), StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class AuthStore {
        public int schemaVersion;
        public long updatedAt;
        public List<UserRecord> users;
    }

    public static class UserRecord {
        public String account;
        public String salt;
        public String passwordPlain;
        public String passwordHash;
        public long createdAt;
        public long updatedAt;
    }
}
