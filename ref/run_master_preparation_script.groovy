static String TOOL_GIT_DIR = 'J:/DEval/source/tools'
static String SCRIPT_DIR = TOOL_GIT_DIR + '/TLANQOS_AutomationSystem/scripts'
static String FUNC_NAME = 'lnd'

def resetToThisCommit(String toolsOrFmain, String sha1Code) {
    dir(toolsOrFmain) {
        bat('git reset --hard ' + sha1Code)
    }
    echo '[MASTER_PREPARE] Reset is done.'
}
def cherrypickThisCommit(String toolsOrFmain, String codeOrCommand, int isCommand) {
    dir(toolsOrFmain) {
        try {
            if (isCommand == 1) {
                bat(codeOrCommand)
            } else {
                bat('git cherry-pick ' + codeOrCommand)
            }
        }
        catch (exc) {
            echo '[MASTER_PREPARE] Error: failed to cherry-pick.'
            bat('git cherry-pick --abort')
            bat('git checkout -f HEAD')
            System.exit(-1)
        }
    }
    echo '[MASTER_PREPARE] Cherry-pick is done.'
}

def applyCommit(String toolsOrFmain, String commitCommand, int isBase) {
    if (commitCommand.length() >= 50) {
        echo '[MASTER_PREPARE] It is a cherry-pick command.'
        int idBegin = commitCommand.indexOf('://')+3
        int idEnd = commitCommand.indexOf('@')
        String beforeId = commitCommand.substring(0,idBegin)
        String afterId = commitCommand.substring(idEnd)
        String cherrypickCommand = beforeId + 'seunghyeon.jo' + afterId
        println('[MASTER_PREPARE] Trying to apply [' + cherrypickCommand + ']...')
        cherrypickThisCommit(toolsOrFmain, cherrypickCommand, 1)
    } else {
        echo '[MASTER_PREPARE] It is a SHA-1 code.'
        if ((isBase == 1) || (commitCommand == "HEAD")) {
            resetToThisCommit(toolsOrFmain, commitCommand)
        } else {
            cherrypickThisCommit(toolsOrFmain, commitCommand, 0)
        }
    }
}

echo '[MASTER_PREPARE] Cloning the up-to-date "Fmain" and "tools" git repositoreis...'
dir('Fmain') {
    git branch: FMAIN_BRANCH, credentialsId: 'seunghyeon.jo', url: 'ssh://seunghyeon.jo@mod.lge.com:29428/mpc55/Fmain.git'
    bat('git pull origin ' + FMAIN_BRANCH)
}

dir('tools') {
    git branch: 'mpc55_release', credentialsId: 'seunghyeon.jo', url: 'ssh://seunghyeon.jo@mod.lge.com:29428/mpc55/tools.git'
    bat('git pull origin mpc55_release')
}

echo '[MASTER_PREPARE] Detaching the current "tools" git head ...'
dir('tools') {
    bat('''git checkout -f HEAD
    git checkout --detach
    '''
    )
}

echo '[MASTER_PREPARE] TOOLS_COMMIT_AS_BASE is being parsed to set a base commit...'
if (TOOLS_COMMIT_AS_BASE != 'HEAD') {
    applyCommit('tools', TOOLS_COMMIT_AS_BASE, 1)
}
echo '[MASTER_PREPARE] TOOLS_COMMITS_TO_CHERRY_PICK is being parsed to cherry-pick commits...'
if (TOOLS_COMMITS_TO_CHERRY_PICK != 'NONE') {
    TOOLS_COMMITS_TO_CHERRY_PICK.split('\n').each {
        param -> println ('[MASTER_PREPARE] Checking [' + param + '] ...' )
        applyCommit('tools', param, 0)
    }
}

echo '[MASTER_PREPARE] Detaching the current "Fmain" git head ...'
dir('Fmain') {
    bat('''git checkout -f HEAD
    git checkout --detach
    '''
    )
}

echo '[MASTER_PREPARE] FMAIN_COMMIT_AS_BASE is being parsed to set a base commit...'
if (FMAIN_COMMIT_AS_BASE != 'HEAD') {
    applyCommit('Fmain', FMAIN_COMMIT_AS_BASE, 1)
}
echo '[MASTER_PREPARE] FMAIN_COMMITS_TO_CHERRY_PICK is being parsed to cherry-pick commits...'
if (FMAIN_COMMITS_TO_CHERRY_PICK != 'NONE') {
    FMAIN_COMMITS_TO_CHERRY_PICK.split('\n').each {
        param -> println ('[MASTER_PREPARE] Checking [' + param + '] ...' )
        applyCommit('Fmain', param, 0)
    }
}

echo '[MASTER_PREPARE] Calling "download_from_cossette.bat"...'
bat('call ' + SCRIPT_DIR + '/download_from_cossette.bat ' 
   + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat')

echo '[MASTER_PREPARE] Calling "store_data_set.bat"...'
bat('call ' + SCRIPT_DIR + '/store_data_set.bat '
    + FUNC_NAME + '\\set_' + FUNC_NAME + '_path.bat ')
String dataSet = readFile('DATA_SET.txt')

return dataSet