#!/bin/sh
if [ $# -ne 2 ];then
    echo "bash ./mount_motional_database.sh [user ID for motional_database] [userpasswd for motional_database]"
    exit 1
fi

USER=$1
PASSWD=$2
MOUNT_DIR='/mnt/motional_database'
echo 'Mount ' $MOUNT_DIR ' with ID: ' $USER $PASSWD
sudo mkdir -p ${MOUNT_DIR}
sudo mount -v -t cifs -o username="${USER}",password="${PASSWD}",vers=2.0,domain=LGE,uid=$(id -u),gid=$(id -g),forceuid,forcegid,sec=ntlmsspi //10.158.10.28/Motional_Database ${MOUNT_DIR}
# sudo mount -v -t cifs -o username="${USER}",vers=2.0,domain=LGE,uid=$(id -u),gid=$(id -g),forceuid,forcegid,sec=ntlmsspi //10.158.10.28/Motional_Database ${MOUNT_DIR}
