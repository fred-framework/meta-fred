#!/bin/bash
# run gdbserver in the target board
# usage: prep_debug.sh 
#   optional arguments:
#     TARGETIP=<target-ip>
#     PACKAGE_NAME=<package-to-be-debugged>
# inspired by: 
#   - https://medium.com/@karel.l.vermeiren/cross-architecture-remote-debugging-using-gdb-with-visual-studio-code-vscode-on-linux-c0572794b4ef
#   - https://variwiki.com/index.php?title=Yocto_Programming_with_VSCode#Remote_Debugging_with_VS_Code

# defines
TARGET_IP=$1
PACKAGE_NAME=$2
TARGET_DIR=/usr/bin/
# build the target
#petalinux-build -c $PACKAGE_NAME
# update the package manager w the new package version
#petalinux-build -c package-index
# In the target, execute the following steps:
#  - kill gdbserver on target
#  - update the package manager internal cache 
#  - replace the old package on target
#  - start gdb on target
ssh root@$TARGET_IP "sh -c '/usr/bin/killall -q gdbserver; dnf update; dnf reinstall -y $PACKAGE_NAME; cd ${TARGET_DIR}; gdbserver localhost:3000 ${PACKAGE_NAME} > /dev/null 2>&1 &'" 
# update the package manager internal cache 
#ssh root@$TARGETIP dnf update
# replace the old package on target
#ssh root@$TARGETIP dnf reinstall -y $PACKAGE_NAME
# start gdb on target
#ssh -n -f root@$TARGETIP "sh -c 'nohup gdbserver localhost:3000 $TARGET_DIR/$PACKAGE_NAME > /dev/null 2>&1 &'"
