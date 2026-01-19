import datetime
import os
import subprocess
import sys
import xml.etree.ElementTree as ET

DEFAULT_ADB_PATH = "/Users/cj/Library/Android/sdk/platform-tools/adb"
PACKAGE_NAME = "com.THLight.BLE.USBeacon.Writer.Simple.dev"
PREFS_PATH = f"/data/data/{PACKAGE_NAME}/shared_prefs/SHARED_PREFERENCES_FILE_LOGIN.xml"
LOG_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "credential-log.txt")


def read_prefs_xml(adb_path: str) -> str:
    cmd = [
        adb_path,
        "exec-out",
        "run-as",
        PACKAGE_NAME,
        "cat",
        PREFS_PATH,
    ]
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if result.returncode != 0 or not result.stdout.strip():
        raise RuntimeError(result.stderr.strip() or "Failed to read SharedPreferences.")
    return result.stdout


def parse_value(root: ET.Element, key: str) -> str:
    node = root.find(f".//string[@name='{key}']")
    return node.text if node is not None else ""


def get_device_serial(adb_path: str) -> str:
    result = subprocess.run([adb_path, "get-serialno"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    serial = result.stdout.strip()
    return serial if serial else "unknown"


def append_log(entry: str) -> None:
    with open(LOG_PATH, "a", encoding="utf-8") as f:
        f.write(entry + "\n")


def main() -> int:
    adb_path = os.environ.get("ADB_PATH", DEFAULT_ADB_PATH)
    try:
        xml_text = read_prefs_xml(adb_path)
        root = ET.fromstring(xml_text)
        user_id = parse_value(root, "SHARED_PREFERENCES_STRING_LAST_USER_ID")
        password = parse_value(root, "SHARED_PREFERENCES_STRING_LAST_PASSWORD")
        access_uuid = parse_value(root, "SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID")
        timestamp = datetime.datetime.now().strftime("%Y/%m/%d %H:%M:%S")
        serial = get_device_serial(adb_path)
        entry = (
            f"{timestamp} | serial={serial} | user_id={user_id} | "
            f"password={password} | access_uuid={access_uuid}"
        )
        append_log(entry)
        print(entry)
        return 0
    except Exception as exc:
        print(f"[log_credentials] {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
