pipeline {
  agent {
    node {
      label 'master'
      customWorkspace 'workspace/master'
    }

  }
  environment {
    dateTime = 'NAtime'
  }
  stages {
    stage('Preparation') {
      steps {
        echo 'Starting Preparation...'
        script {
          dateTime = load 'run_function_get_date_time.groovy'
          println('Current time is ' + dateTime)
        }

        sh 'mkdir -p e2e-ml'
        dir(path: 'e2e-ml') {
          git(url: 'http://mod.lge.com/hub/taekkyun.kim/e2e-ml.git', credentialsId: 'seunghyeon.jo', branch: 'master', changelog: true, poll: true)
        }

      }
    }

    stage('Preprocess') {
      steps {
        echo 'Starting Preprocessing...'
        sh 'echo gpu_or_cpu:' + use_gpu + ', quantization_bit:' + quantization_bit + ', number_of_images: ' + number_of_images
        sh './build_docker_to_set_params.sh'
        sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ use_gpu ' + use_gpu
        sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ image_count ' + number_of_images
        sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ coeff_flags coeff-force-fx' + quantization_bit
        sh './run_docker_to_set_params.sh ${PWD}/e2e-ml/deploy/ambarella/ data_format act-force-fx' + quantization_bit_activation
        sh './remove_docker_to_set_params.sh'
      }
    }

    stage('ModelEngineering') {
      steps {
        echo 'Starting Model Engineering...'
        sh '''cd e2e-ml/deploy/
./onnx_to_bin.py --model /mnt/motional_database/'''+model_for_quantization+' --dataset /mnt/motional_database/'+dataset_for_quantization
      }
    }

    stage('ModelEvaluation') {
      steps {
        echo 'Starting Model Evaluation...'
      }
    }

    stage('ModelDeploy') {
      steps {
        echo 'Starting Model Deploying...'
      }
    }

    stage('Postprocess') {
      steps {
        echo 'Start Postprocessing...'
        sh 'ls -al /var/jenkins_home/out/'
        sh 'ls -al e2e-ml/deploy/ambarella/out/cavalry_out/'
        sh '''mv e2e-ml/deploy/ambarella/out/cavalry_out/lgenet_cavalry.bin /var/jenkins_home/out
rm -rf e2e-ml'''
      }
    }

  }
  post {
    always {
      sh '''# docker stop  $(docker ps -a -q --filter ancestor=ambarella --format="{{.ID}}")
docker rmi ambarella
rm -rf e2e-ml@tmp'''
      cleanWs(cleanWhenNotBuilt: false, deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true, patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                                                                                                                                                          [pattern: '.propsfile', type: 'EXCLUDE']])
    }

  }
  parameters {
    string(name: 'number_of_images', defaultValue: '1', description: 'Number of images to be referenced')
    string(name: 'model_for_quantization', defaultValue: '05.Model/intel_openpose.onnx')
    string(name: 'dataset_for_quantization', defaultValue: '02.Dataset/11.Quantization_image/01.seatbelt/')
    choice(name: 'use_gpu', choices: ['Yes', 'No'], description: 'GPU utilization')
    choice(name: 'quantization_bit', choices: ['16', '8'], description: 'Number of bits for representing the integer (16-bit / 8-bit fixed point)')
    choice(name: 'quantization_bit_activation', choices: ['16', '8'], description: 'Number of bits for representing the integer for activation (16-bit / 8-bit fixed point)')
  }

}