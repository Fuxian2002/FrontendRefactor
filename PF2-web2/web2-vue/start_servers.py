import subprocess
import os
import time
import sys

# Define paths
base_dir = os.path.dirname(os.path.abspath(__file__))
# web2 is current dir
web2_dir = base_dir
# web1 is ../../PF2-web1-main/PF2-web1-new relative to web2
# But let's check the path structure carefully from previous LS
# d:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\PF2-web2\web2-vue
# Sibling is PF2-web1-main/PF2-web1-new
# So ../../PF2-web1-main/PF2-web1-new is correct.

web1_dir = os.path.abspath(os.path.join(base_dir, "../../PF2-web1-main/PF2-web1-new"))

print(f"Web1 dir: {web1_dir}")
print(f"Web2 dir: {web2_dir}")

def start_server(directory, name, port):
    print(f"Starting {name} in {directory} on port {port}...")
    try:
        # Use shell=True to run npm run dev
        # Use creationflags=subprocess.CREATE_NEW_CONSOLE to open new window if possible?
        # But we want to capture output or just let it run.
        # Since we are in a headless env effectively, let's just run it.
        
        # Check if node_modules exists
        if not os.path.exists(os.path.join(directory, "node_modules")):
            print(f"Installing dependencies for {name}...")
            subprocess.run("npm install", shell=True, cwd=directory, check=True)
            
        # Start server
        # We use Popen to run in background
        proc = subprocess.Popen("npm run dev", shell=True, cwd=directory, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return proc
    except Exception as e:
        print(f"Failed to start {name}: {e}")
        return None

# Start Web1
web1_proc = start_server(web1_dir, "Web1", 5173)

# Start Web2
web2_proc = start_server(web2_dir, "Web2", 5174)

print("Servers started. Monitoring...")

try:
    while True:
        if web1_proc:
            if web1_proc.poll() is not None:
                print(f"Web1 exited with code {web1_proc.returncode}")
                # Read stderr
                print(web1_proc.stderr.read().decode('utf-8', errors='ignore'))
                web1_proc = None
        
        if web2_proc:
            if web2_proc.poll() is not None:
                print(f"Web2 exited with code {web2_proc.returncode}")
                print(web2_proc.stderr.read().decode('utf-8', errors='ignore'))
                web2_proc = None
                
        if not web1_proc and not web2_proc:
            print("Both servers exited.")
            break
            
        time.sleep(1)
except KeyboardInterrupt:
    print("Stopping servers...")
    if web1_proc: web1_proc.terminate()
    if web2_proc: web2_proc.terminate()
