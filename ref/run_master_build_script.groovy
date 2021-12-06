static String TOOL_GIT_DIR = 'J:/DEval/source/tools'
static String SCRIPT_DIR = TOOL_GIT_DIR + '/TLANQOS_AutomationSystem/scripts'
static String FUNC_NAME = 'lnd'

echo '[MASTER_BUILD] Calling "build_binary.bat"...'
bat('call ' + SCRIPT_DIR + '/build_binary.bat')

echo '[MASTER_BUILD] Calling "copy_binary.bat"...'
bat('call ' + SCRIPT_DIR + '/copy_binary.bat '
    + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ' + DATE_TIME)

// Run the maven build
//if (isUnix()) {
// sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
//} else {
// bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
//}