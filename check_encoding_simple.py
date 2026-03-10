import sys

def check_file_encoding(path):
    try:
        with open(path, 'rb') as f:
            raw = f.read()
            # Try to decode as UTF-8
            try:
                decoded = raw.decode('utf-8')
                print(f"{path}: Valid UTF-8")
                # Check for BOM
                if raw.startswith(b'\xef\xbb\xbf'):
                    print(f"{path}: UTF-8 BOM detected")
            except UnicodeDecodeError:
                print(f"{path}: NOT valid UTF-8")
                # Try GBK
                try:
                    decoded = raw.decode('gbk')
                    print(f"{path}: Valid GBK")
                except UnicodeDecodeError:
                    print(f"{path}: Unknown encoding")
    except Exception as e:
        print(f"Error reading {path}: {e}")

check_file_encoding(r'D:\??????\????ùù1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\PF2-web2\web2-vue\src\components\RightBar.vue')
check_file_encoding(r'D:\??????\????ùù1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\PF2-web2\PF2-web5\web5-vue\src\components\RightBar.vue')
