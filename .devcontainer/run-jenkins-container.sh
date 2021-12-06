JENKINS_DATA=${PWD}/../../../workspace/jenkins_home/data

sudo chown -R 1000.1000 ${JENKINS_DATA}
sudo chmod 777 ${JENKINS_DATA}/..

docker build -t jenkins-image . && \
docker run \
	--name jenkins-container \
	--rm \
	--detach \
	--network jenkins \
	--env DOCKER_HOST=tcp://docker:2376 \
	--env DOCKER_CERT_PATH=/certs/client \
	--env DOCKER_TLS_VERIFY=1 \
	--publish 8080:8080 \
	--publish 50000:50000 \
	--volume ${JENKINS_DATA}:/var/jenkins_home \
	--volume ${JENKINS_DATA}/..:/var/jenkins_out \
	--volume jenkins-docker-certs:/certs/client \
	--volume /mnt/motional_database:/mnt/motional_database \
	--workdir=/var/jenkins_home \
	jenkins-image
	#jenkins-image \
	#/bin/bash 
	# --publish 8080:8080 \
	# -it \
	# --net=host \
	# --privileged \
	# --env QT_X11_NO_MITSHM=1 \
	# --security-opt \
	# seccomp=unconfined \
	# --volume=/tmp/.X11-unix:/tmp/.X11-unix:rw \
	# --cap-add=SYS_PTRACE \
	# --group-add=plugdev \
	# --group-add=video \

