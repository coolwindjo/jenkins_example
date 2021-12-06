JENKINS_DATA=${PWD}/../../../workspace/jenkins_home/data

docker network create jenkins

docker run \
	--name jenkins-docker \
	--rm \
	--detach \
	--privileged \
	--network jenkins \
	--network-alias docker \
	--env DOCKER_TLS_CERTDIR=/certs \
	--volume jenkins-docker-certs:/certs/client \
	--volume ${JENKINS_DATA}:/var/jenkins_home \
	--volume ${JENKINS_DATA}/..:/var/jenkins_out \
	--volume /mnt/motional_database:/mnt/motional_database \
	--publish 2376:2376 \
	docker:dind \
	--storage-driver overlay2
