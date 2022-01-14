#!/bin/sh
if [ $# -ne 2 ];then
    echo "bash ./mount_directory.sh [user ID for the directory] [userpasswd for the directory]"
    exit 1
fi

USER=$1
PASSWD=$2
MOUNT_DIR_V='/mnt/Vision_AI_NAS'
MOUNT_DIR_M='/mnt/Motional_Database'
echo 'Mount ' $MOUNT_DIR_V ' with ID: ' $USER $PASSWD
sudo mkdir -p ${MOUNT_DIR_V}
sudo mount -v -t cifs -o username="${USER}",password="${PASSWD}",vers=2.0,domain=LGE,uid=$(id -u),gid=$(id -g),forceuid,forcegid,sec=ntlmsspi //10.158.10.45/Vision_AI_Cabin_Algorithm ${MOUNT_DIR_V}
echo 'Mount ' $MOUNT_DIR_M ' with ID: ' $USER $PASSWD
sudo mkdir -p ${MOUNT_DIR_M}
sudo mount -v -t cifs -o username="${USER}",password="${PASSWD}",vers=2.0,domain=LGE,uid=$(id -u),gid=$(id -g),forceuid,forcegid,sec=ntlmsspi //10.158.10.28/Motional_Database ${MOUNT_DIR_M}
