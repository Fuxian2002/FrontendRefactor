# -*- coding: utf-8 -*-
import os

def check_file_encoding(path):
    print(f"Checking: {path}")
    if not os.path.exists(path):
        print("File not found")
        return

    try:
        with open(path, 'rb') as f:
            raw = f.read()
            # Try to decode as UTF-8
            try:
                decoded = raw.decode('utf-8')
                print(f"Result: Valid UTF-8")
                # Check for BOM
                if raw.startswith(b'\xef\xbb\xbf'):
                    print(f"Result: UTF-8 BOM detected")
            except UnicodeDecodeError:
                print(f"Result: NOT valid UTF-8")
                # Try GBK
                try:
                    decoded = raw.decode('gbk')
                    print(f"Result: Valid GBK")
                except UnicodeDecodeError:
                    print(f"Result: Unknown encoding")
    except Exception as e:
        print(f"Error reading {path}: {e}")

# Use relative paths
web2_path = os.path.join('web2-vue', 'src', 'components', 'RightBar.vue')
web5_path = os.path.join('PF2-web5', 'web5-vue', 'src', 'components', 'RightBar.vue')

check_file_encoding(web2_path)
check_file_encoding(web5_path)
