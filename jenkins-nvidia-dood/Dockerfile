# FROM ubuntu:14.04

# # Let's start with some basic stuff.
# RUN apt-get update -qq && apt-get install -qqy \
# 	apt-transport-https \
# 	ca-certificates \
# 	curl \
# 	git \
# 	iptables \
# 	libxext-dev libxrender-dev libxtst-dev \
# 	ssh-askpass \
# 	unzip \
# 	wget \
# 	zip

# # Install Docker from Docker Inc. repositories.
# ARG DOCKER_VERSION=1.10.3
# RUN curl -L -O https://get.docker.com/builds/Linux/x86_64/docker-${DOCKER_VERSION}.tgz \
# 	&& tar zxf docker-${DOCKER_VERSION}.tgz -C /

# # Install Docker Compose
# ENV DOCKER_COMPOSE_VERSION 1.3.3
# RUN curl -L https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose \
# 	&& chmod +x /usr/local/bin/docker-compose

# # Install Jenkins
# ENV JENKINS_HOME=/var/lib/jenkins JENKINS_UC=https://updates.jenkins-ci.org HOME="/var/lib/jenkins"
# RUN wget --progress=bar:force -O - https://jenkins-ci.org/debian/jenkins-ci.org.key | apt-key add - \
# 	&& sh -c 'echo deb http://pkg.jenkins-ci.org/debian binary/ > /etc/apt/sources.list.d/jenkins.list' \
# 	&& apt-get update && apt-get install -y jenkins \
# 	&& apt-get clean \
# 	&& apt-get purge \
# 	&& rm -rf /var/lib/apt/lists/*

# # Make the jenkins user a sudoer
# # Replace the docker binary with a sudo script
# RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers \
# 	&& mv /usr/local/bin/docker /usr/local/bin/docker.bin \
# 	&& printf '#!/bin/bash\nsudo docker.bin "$@"\n' > /usr/local/bin/docker \
# 	&& chmod +x /usr/local/bin/docker

# # Copy basic configuration into jenkins
# COPY config.xml credentials.xml hudson.tasks.Ant.xml hudson.tasks.Maven.xml plugins.txt $JENKINS_HOME/

# # Install Jenkins plugins from the specified list
# # Install jobs & setup ownership & links
# COPY plugins.sh /usr/local/bin/plugins.sh
# COPY jobs/. $JENKINS_HOME/jobs
# RUN chmod +x /usr/local/bin/plugins.sh; sleep 1 \
# 	&& /usr/local/bin/plugins.sh $JENKINS_HOME/plugins.txt \
# 	&& chown -R jenkins:jenkins /var/lib/jenkins

# # Define the workspace - assuming the path does not contain #
# ARG WORKSPACE='${ITEM_ROOTDIR}\/workspace'
# RUN sed -i -- "s#\${ITEM_ROOTDIR}/workspace#${WORKSPACE}#" $JENKINS_HOME/config.xml

# # Expose Jenkins default port
# EXPOSE 8080

# # Become the jenkins user (who thinks sudo is not needed for docker commands)
# USER jenkins
# WORKDIR /var/lib/jenkins

# # Start the war
# CMD ["java", "-jar", "/usr/share/jenkins/jenkins.war"]



ARG tag=${DOCKER_TAG:-lts}
FROM jenkins/jenkins:${tag}
# FROM jenkins/jenkins:2.303.2-jdk11

# if we want to install via apt
USER root

# Install packages
RUN apt-get update && apt-get install -q -y --no-install-recommends \
	libltdl7 \
	apt-transport-https \
	ca-certificates curl gnupg2 \
	software-properties-common \
	tar \
	xz-utils \
	bash-completion \
	vim \
	python3 \
	&& apt-get clean && rm -rf /var/lib/apt/lists/*
	# sudo supervisor \

# RUN curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg \
# 	| apt-key add - \
# 	&& add-apt-repository \
# 	"deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
# 	$(lsb_release -cs) \
# 	stable" \
# 	&& apt-get update \
# 	&& apt-get install -y docker-ce \
# 	&& apt-get clean && rm -rf /var/lib/apt/lists/*

# RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
# RUN apt-key fingerprint 0EBFCD88
# RUN add-apt-repository \
#        "deb [arch=amd64] https://download.docker.com/linux/debian \
#        $(lsb_release -cs) stable"
# RUN apt-get update && apt-get install -y docker-ce-cli


# Create log folder for supervisor and jenkins
#RUN mkdir -p /var/log/supervisor
#RUN mkdir -p /var/log/jenkins

# Copy the supervisor.conf file into Docker
#COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Start supervisord when running the container
#CMD /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf

# Add jenkins user to docker group to make sure jenkins user has docker privileges
# RUN usermod -aG docker jenkins
# getent group docker | awk -F: '{printf "%d\n", $3}'
#RUN dockerGid=$(getent group docker | awk -F: '{printf "%d\n", $3}')
#RUN dockerGid=$(awk -F\: '/docker/ {print $3}' /etc/group)
#ARG dockerGid=999
ARG dockerGid=1013
RUN echo "${dockerGid}"
RUN echo "docker:x:${dockerGid}:jenkins" >> /etc/group

# drop back to the regular jenkins user - good practice
USER jenkins

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugins < /usr/share/jenkins/ref/plugins.txt
# RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
# Install Jenkins plugins
	# "blueocean:1.25.1" \
	# "docker-workflow:1.26" \
# 	"extended-choice-parameter" \
# 	"role-strategy" \
# 	"pipeline-model-definition"

# COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy
