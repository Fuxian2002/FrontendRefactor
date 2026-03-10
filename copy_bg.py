# -*- coding: utf-8 -*-
import shutil
import os

# Use raw strings for paths to avoid escape character issues
src = r"D:\??????\????ùù1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\PF2-web2\web2-vue\src\assets\img\be30d4cc6139d1059cfeb21b30d15bcc copy.png"
dst = r"D:\??????\????ùù1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\PF2-web2\PF2-web5\web5-vue\src\assets\img\background.png"

try:
    if os.path.exists(src):
        shutil.copy2(src, dst)
        print("Copy success")
    else:
        print(f"Source not found: {src}")
except Exception as e:
    print(f"Copy failed: {e}")
