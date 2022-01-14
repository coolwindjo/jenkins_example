JENKINS_DATA=/var/jenkins_home
#DOCKER_GID=$(awk -F\: '/docker/ {print $3}' /etc/group)
#echo ${DOCKER_GID}

cd jenkins-nvidia-dood

sudo mkdir -p ${JENKINS_DATA}
sudo mkdir -p ${JENKINS_DATA}/out
sudo cp .gitconfig ${JENKINS_DATA}
sudo tar -xzvf ssh_mun.tar.gz -C ${JENKINS_DATA}/
sudo chown -R 1000.1000 ${JENKINS_DATA}

# docker network create jenkins

docker build \
	--build-arg dockerGid=$(awk -F\: '/docker/ {print $3}' /etc/group) \
	-t jenkins-dood-image . && \
	#--restart=always \
docker run \
	--rm \
	--name jenkins-dood-container \
	--detach \
	--publish 8080:8080 \
	--publish 50000:50000 \
	--volume $(which docker):/usr/bin/docker \
	--volume /var/run/docker.sock:/var/run/docker.sock \
	--volume ${JENKINS_DATA}:/var/jenkins_home \
	--volume ${JENKINS_DATA}/out:/var/jenkins_home/out \
	--volume /mnt/Vision_AI_NAS:/mnt/Vision_AI_NAS \
	--volume /mnt/Motional_Database:/mnt/Motional_Database \
	--workdir=/var/jenkins_home \
	jenkins-dood-image
	# -it \
	# jenkins-dood-image \
	# /bin/bash
	# -e "TZ=America/Chicago"
	# --volume ${JENKINS_DATA}:/var/jenkins_home \	# --volume $HOME/jenkins:/var/jenkins_home would map the container’s /var/jenkins_home directory to the jenkins subdirectory within the $HOME directory on your local machine, which would typically be /Users/<your-username>/jenkins or /home/<your-username>/jenkins. Note that if you change the source volume or directory for this, the volume from the docker:dind container above needs to be updated to match this.
	# --privileged \
	# --network jenkins \
	# --network-alias docker \	# Makes the Docker in Docker container available as the hostname docker within the jenkins network.
	# --env DOCKER_TLS_CERTDIR=/certs \	# Enables the use of TLS in the Docker server. Due to the use of a privileged container, this is recommended, though it requires the use of the shared volume described below. This environment variable controls the root directory where Docker TLS certificates are managed.
	# --env DOCKER_CERT_PATH=/certs/client \
	# --env DOCKER_TLS_VERIFY=1 \
	# --env DOCKER_HOST=tcp://docker:2376 \	# 	Specifies the environment variables used by docker, docker-compose, and other Docker tools to connect to the Docker daemon from the previous step.
	# --volume jenkins-docker-certs:/certs/client \
	# --publish 2376:2376 \	# ( Optional ) Exposes the Docker daemon port on the host machine. This is useful for executing docker commands on the host machine to control this inner Docker daemon.
	# docker:dind

# ./stop-nvidia-dood-container.sh
