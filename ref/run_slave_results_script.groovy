static String TOOL_GIT_DIR = 'J:/DEval/source/tools'
static String SCRIPT_DIR = TOOL_GIT_DIR + '/TLANQOS_AutomationSystem/scripts'
static String FUNC_NAME = 'lnd'

// Send KPI.xmls for being imported to Cosette
echo '[SLAVE_RESULTS] Calling "import_KPI.bat"...'
bat('call ' + SCRIPT_DIR + '/import_KPI.bat '
    + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ' + DATE_TIME + ' ' + DATA_SET)

//junit '**/target/surefire-reports/TEST-*.xml'
//archive 'target/*.jar'
