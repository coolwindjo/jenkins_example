FROM jenkins/jenkins:2.303.2-jdk11
# if we want to install via apt
USER root

# install packages
RUN apt-get update && apt-get install -q -y --no-install-recommends \
	apt-transport-https \ 
	ca-certificates curl gnupg2 \ 
	software-properties-common \ 
	tar \ 
	xz-utils \ 
	bash-completion \
	vim \
	&& apt-get clean && rm -rf /var/lib/apt/lists/*

	#snapd \ 
	#python \ 
	#python3-pip \ 
	#python3-mako \ 
	#dirmngr \ 
	#gdb g++-6 \ 
	#autoconf automake \ 
	#libssl-dev u-boot-tools dos2unix doxygen \ 
	# libc6:i386 \ 
	#libdb1-compat \ 
	# libstdc++6:i386 libz1:i386 \ 
	# libncurses5:i386 \

RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN apt-key fingerprint 0EBFCD88
RUN add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/debian \
       $(lsb_release -cs) stable"
RUN apt-get update && apt-get install -y docker-ce-cli

# drop back to the regular jenkins user - good practice
USER jenkins
RUN jenkins-plugin-cli --plugins \
	"blueocean:1.25.1" \
	"docker-workflow:1.26" \
	"extended-choice-parameter" \
	"role-strategy" \
	"pipeline-model-definition" 

COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy

