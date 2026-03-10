# -*- coding: utf-8 -*-
import os

def convert_to_utf8(path):
    print(f"Processing: {path}")
    if not os.path.exists(path):
        print("File not found")
        return

    try:
        with open(path, 'rb') as f:
            raw = f.read()
            
        content = None
        try:
            content = raw.decode('utf-8')
            print("File is already UTF-8")
            # Ensure it stays UTF-8 by rewriting (just in case)
            # Actually if it's already UTF-8, we might not need to do anything, 
            # but let's rewrite to be sure it's clean UTF-8 without BOM if possible.
        except UnicodeDecodeError:
            try:
                content = raw.decode('gbk')
                print("File is GBK, converting to UTF-8...")
            except UnicodeDecodeError:
                print("File is unknown encoding, cannot convert safely.")
                return

        if content is not None:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print("Successfully converted/verified as UTF-8")

    except Exception as e:
        print(f"Error processing {path}: {e}")

# Paths
web5_rightbar = os.path.join('PF2-web5', 'web5-vue', 'src', 'components', 'RightBar.vue')
web5_topbar = os.path.join('PF2-web5', 'web5-vue', 'src', 'components', 'TopBar.vue')

convert_to_utf8(web5_rightbar)
convert_to_utf8(web5_topbar)
