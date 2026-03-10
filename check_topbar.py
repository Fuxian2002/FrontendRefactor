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
            try:
                decoded = raw.decode('utf-8')
                print(f"Result: Valid UTF-8")
            except UnicodeDecodeError:
                print(f"Result: NOT valid UTF-8")
                try:
                    decoded = raw.decode('gbk')
                    print(f"Result: Valid GBK")
                except UnicodeDecodeError:
                    print(f"Result: Unknown encoding")
    except Exception as e:
        print(f"Error reading {path}: {e}")

check_file_encoding(os.path.join('PF2-web5', 'web5-vue', 'src', 'components', 'TopBar.vue'))
