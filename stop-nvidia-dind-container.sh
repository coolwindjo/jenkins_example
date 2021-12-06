docker stop nvidia-dind-container

docker rmi nvidia-dind-image

docker network rm jenkins

docker volume rm jenkins-docker-certs
