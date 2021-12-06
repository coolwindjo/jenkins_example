static String TOOL_GIT_DIR = 'J:/DEval/source/tools'
static String SCRIPT_DIR = TOOL_GIT_DIR + '/TLANQOS_AutomationSystem/scripts'
static String FUNC_NAME = 'lnd'

echo '[SLAVE_BUILD] Now starting KPI evaluation...'
parallel (
    "adas_mpc55" : {
        // Run the adas_mpc55 binary
        bat('call ' + SCRIPT_DIR + '/run_mpc55.bat '
            + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ' + DATE_TIME + ' ' + DATA_SET)
    },
    "evaluator" : {
        // Run the evaluation tools
        bat('call ' + SCRIPT_DIR + '/run_evaluation.bat '
            + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ' + DATE_TIME + ' ' + DATA_SET)
    }
)

// Run the maven build
//if (isUnix()) {
// sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
//} else {
// bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
//}

