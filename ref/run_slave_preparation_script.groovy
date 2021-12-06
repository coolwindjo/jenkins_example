static String TOOL_GIT_DIR = 'J:/DEval/source/tools'
static String SCRIPT_DIR = TOOL_GIT_DIR + '/TLANQOS_AutomationSystem/scripts'
static String FUNC_NAME = 'lnd'

echo '[SLAVE_PREPARE] Checking IP address of the current slave...'
bat('ipconfig')

// Get DAT files and GT.xmls
echo '[SLAVE_PREPARE] Calling "copy_from_master.bat"...'
bat('call ' + SCRIPT_DIR + '/copy_from_master.bat '
    + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ' + DATE_TIME + ' ' + DATA_SET)

// Get some code from a GitHub repository
//git 'https://github.com/jglick/simple-maven-project-with-tests.git'

