import os

path = 'src/assets/theme.css'
print(f"Reading {os.path.abspath(path)}")

try:
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()

    print(f"Original length: {len(content)}")
    old_content = content

    # Replace fonts
    content = content.replace("'MiSans-Regular'", "'Microsoft YaHei'")
    content = content.replace("'MiSans-Medium'", "'Microsoft YaHei'")
    content = content.replace("'FRIZON'", "'Microsoft YaHei'")
    content = content.replace("'AlimamaShuHeiTi-Bold'", "'Microsoft YaHei'")

    # Fix double occurrences resulting from replacement
    content = content.replace("'Microsoft YaHei', 'Microsoft YaHei'", "'Microsoft YaHei'")

    if content != old_content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print("File updated successfully.")
    else:
        print("No changes needed.")

except Exception as e:
    print(f"Error: {e}")
