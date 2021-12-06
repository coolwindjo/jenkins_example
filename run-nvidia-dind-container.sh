JENKINS_DATA=${PWD}/../../workspace/jenkins_home/data

docker network create jenkins

cd ./nvidia-dind && \
docker build -t nvidia-dind-image . && \
docker run \
	--name nvidia-dind-container \
	--rm \
	--detach \
	--privileged \
	--gpus 1 \
	--network jenkins \
	--network-alias docker \
	--env DOCKER_TLS_CERTDIR=/certs \
	--volume jenkins-docker-certs:/certs/client \
	--volume ${JENKINS_DATA}:/var/jenkins_home \
	--volume ${JENKINS_DATA}/..:/var/jenkins_out \
	--volume /mnt/motional_database:/mnt/motional_database \
	--publish 2376:2376 \
	nvidia-dind-image:latest \
	--storage-driver overlay2

	#-it \
	#/bin/bash
