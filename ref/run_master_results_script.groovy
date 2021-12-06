echo '[MASTER_RESULTS] Checking out to the previous branches...'
dir('tools') {
    bat('git clean -fdx')
    bat('git checkout -')
}
dir('Fmain') {
    bat('git reset --hard HEAD')
    bat('git checkout -')
}

//junit '**/target/surefire-reports/TEST-*.xml'
//archive 'target/*.jar'
