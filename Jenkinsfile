
def resetToThisCommit(String sha1Code) {
    sh 'git reset --hard ' + sha1Code
    echo 'Reset is done.'
}
def cherrypickThisCommit(String codeOrCommand, int isCommand) {
    try {
        if (isCommand == 1) {
            sh codeOrCommand
        } else {
            sh 'git cherry-pick ' + codeOrCommand
        }
    }
    catch (exc) {
        echo 'Error: failed to cherry-pick.'
        sh 'git cherry-pick --abort'
        sh 'git checkout -f HEAD'
        System.exit(-1)
    }
    echo 'Cherry-pick is done.'
}

def applyCommit(String commitCommand, int isBase) {
    if (commitCommand.length() >= 50) {
        echo 'It is a cherry-pick command.'
        int idBegin = commitCommand.indexOf('://')+3
        int idEnd = commitCommand.indexOf('@')
        String beforeId = commitCommand.substring(0,idBegin)
        String afterId = commitCommand.substring(idEnd)
        String cherrypickCommand = beforeId + credential_id + afterId
        println('Trying to apply [' + cherrypickCommand + ']...')
        cherrypickThisCommit(cherrypickCommand, 1)
    } else {
        echo 'It is a SHA-1 code.'
        if (isBase == 1) {
            resetToThisCommit(commitCommand)
        } else {
            cherrypickThisCommit(commitCommand, 0)
        }
    }
}

pipeline {
  agent {
    node {
      label 'master'
      customWorkspace 'workspace/master'
    }

  }
  environment {
    dateTime = 'NAtime'
    gpu_arg = " --env NVIDIA_VISIBLE_DEVICES=all --env NVIDIA_DRIVER_CAPABILITIES=all --gpus "
  }
  stages {
    stage('Preparation') {
      steps {
        echo 'Starting Preparation...'
        script {
          dateTime = load 'run_function_get_date_time.groovy'
          println('Current time is ' + dateTime)
          println('branch_name is ' + branch_name)
          if (sub_directory == 'N/A') {
            git branch: branch_name, credentialsId: credential_id, url: 'ssh://'+credential_id+'@vgit.lge.com:29450/'+git_name
          } else {
            dir(path: git_name) {
              checkout([$class: 'GitSCM',
                branches: [[name: '*/' + branch_name]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [[
                  $class: 'SparseCheckoutPaths',
                  sparseCheckoutPaths:[[$class: 'SparseCheckoutPath', path: sub_directory]]
                ]],
                submoduleCfg: [],
                userRemoteConfigs: [[
                  credentialsId: credential_id,
                  url: 'ssh://' + credential_id + '@vgit.lge.com:29450/' + git_name
                ]]
              ])
              script {
                if (commit_as_base != 'HEAD') {
                  echo 'commit_as_base is being parsed to set a base commit...'
                  applyCommit(commit_as_base, 1)
                }
                if (commits_to_cherrypick != 'NONE') {
                  echo 'commits_to_cherrypick is being parsed to cherry-pick commits...'
                  commits_to_cherrypick.split(';').each {
                    param -> println ('Checking [' + param + '] ...' )
                    applyCommit(param, 0)
                  }
                }
              }
            }
          }
        }
      }
    }

    stage('Preprocess') {
      steps {
        echo 'Starting Preprocess...'

        script {
          int idDF = dockerfile_path.indexOf('Dockerfile')
          dockerfile_dir = dockerfile_path.substring(0,idDF)
        }
        dir(path: git_name) {
          dir(path: dockerfile_dir){
            sh 'docker build -t image_'+dateTime+ ' .'
          }
        }
        // sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ use_gpu ' + use_gpu
        // sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ image_count ' + number_of_images
        // sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ coeff_flags coeff-force-fx' + quantization_bit
        // sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ data_format act-force-fx' + quantization_bit_activation
      }
    }

    stage('DockerDeploy') {
      steps {
        echo 'Starting Docker Deploy...'
        script {
          if (gpu_number == "None") {
            gpu_arg=" "
            gpu_number=" "
          } else if (gpu_number != "All") {
            gpu_arg+="\'\"device="
            gpu_number+="\"\'"
          }
          println("gpu_arg = " + gpu_arg)
          println("gpu_number = " + gpu_number)
        }
        sh 'docker run --rm '+ gpu_arg + gpu_number + docker_run_args +
        ' -v ${PWD}/'+working_path+':/workspaces --workdir=/workspaces --name=container_'+dateTime +
        ' image_'+dateTime + ' /bin/bash'

        timeout(time: expected_time, unit: 'HOURS') {
          waitUntil(initialRecurrencePeriod: 15000) {
            script {
              status = sh(returnStdout: true, script: "echo \$(docker ps -a -f name=container_"+dateTime + " | wc -l)").trim()
              println("The status is " + status)
              if ( status != "1") {
                println("A container is still running, please copy the following command and paste to the SSH terminal to access to the container.")
                println("[ACCESS]\ndocker exec -it container_"+dateTime + " /bin/bash")
                println("[STOP]\ndocker stop container_"+dateTime)
                return false
              } else {
                return true
              }
            }
          }
        }
      }
    }

    stage('Postprocess') {
      steps {
        echo 'Start Postprocess...'
        sh 'ls -al /var/jenkins_home/out/'
        sh 'rm -rf '+ git_name
      }
    }
  }
  post {
    always {
      script {
        status = sh(returnStdout: true, script: "echo \$(docker ps -a -f name=container_"+dateTime + " | wc -l)").trim()
        println("The status is " + status)
        if ( status != "1") {
          // sh 'docker stop  $(docker ps -a -q --filter ancestor=image_'+dateTime + ' --format="{{.ID}}")'
          sh 'docker stop container_'+dateTime
        }
        if (reuse_nexttime == 'No') {
          echo 'image_'+dateTime + 'is being removed...'
          sh 'docker rmi image_' + dateTime
        }
      }
      sh 'rm -rf '+git_name+'@tmp'
      cleanWs(cleanWhenNotBuilt: false, deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true, patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
        [pattern: '.propsfile', type: 'EXCLUDE']]
      )
    }
  }

  parameters {
    choice(name: 'expected_time', choices: [1, 2, 3, 4, 5, 6, 7, 8, 24, 48, 72, 96, 120], description: 'Expected time to use a container (hour)')
    string(name: 'git_name', defaultValue: 'oms/cvlib', description: 'Name to clone git repositories under the @vgit.lge.com:29450/')
    string(name: 'sub_directory', defaultValue: 'tools/CDPP/cics/tools/debug_gui', description: 'Relative path to be cloned (N/A for whole repository)')
    string(name: 'branch_name', defaultValue: 'adas_oms_release', description: 'Name of git branch to checkout')
    string(name: 'commit_as_base', defaultValue: 'HEAD', description: 'SHA-1 code of the base commit to start with')
    string(name: 'commits_to_cherrypick', defaultValue: 'NONE', description: 'SHA-1 code of commits to cherry-pick OR cherry-pick commands copied from the Gerrit')
    string(name: 'dockerfile_path', defaultValue: 'tools/CDPP/cics/tools/debug_gui/.devcontainer/Dockerfile', description: 'Relative path to a Dockerfile in the given git repository')
    string(name: 'working_path', defaultValue: 'oms/cvlib/tools/CDPP/cics/tools/debug_gui/', description: 'Relative path to a working directory')
    choice(name: 'reuse_nexttime', choices: ['No', 'Yes'], description: 'If you want to use the built container image next time, choose Yes')
    string(name: 'docker_run_args', defaultValue: ''' -i -d \
    --shm-size=64G -v /raid/data:/data  -p 7000:22 \
    --volume=/mnt/Vision_AI_NAS:/mnt/Vision_AI_NAS \
    --volume=/mnt/Motional_Database:/mnt/Motional_Database \
    --net=host \
    --privileged \
    --env DISPLAY=$DISPLAY \
    --env="QT_X11_NO_MITSHM=1" \
    --cap-add=SYS_PTRACE \
    --security-opt seccomp=unconfined \
    --volume=/tmp/.X11-unix:/tmp/.X11-unix:rw \
    --group-add=plugdev \
    --group-add=video ''')
    choice(name: 'gpu_number', choices: ['All', 'None', '0', '1', '2', '3', '4', '5', '6', '7'], description: 'GPU number to be utilized')
    string(name: 'credential_id', defaultValue: 'junhouk.mun', description: 'Registered ID for cloning git repositories')
  }
}