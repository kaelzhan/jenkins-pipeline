
def check_status = true
pipeline {
    agent any
    triggers { pollSCM('* * * * *') }
    stages {
        stage('Check branch'){
            when { branch 'master' }
//            expression { BRANCH_NAME ==~ /feature\/[0-9]+\.[0-9]+\.[0-9]+/ }
            steps {
                script {
                    check_status = true
                }
            }
        }
        stage('Get Repo') {
            when {
                expression {check_status == true}
            }
            steps {
                dir('current_version_repo') {
//                    git branch: env.BRANCH_NAME, url: 'https://github.com/ppoooolu/test-pipline.git', credentialsId: ''
                    git branch: 'test1', url: 'https://github.com/ppoooolu/test-pipline.git', credentialsId: ''
                }

                dir('master_version_repo') {
                    git branch: 'master', url: 'https://github.com/ppoooolu/test-pipline.git', credentialsId: ''
                }
                script{
                    current_version_list = readFile("./current_version_repo/version").split('\n')
                    master_version_list = readFile("./master_version_repo/version").split('\n')

                    echo "current_version_list:"
                    sh 'cat ./current_version_repo/version'

                    echo "master_version_list:"
                    sh 'cat ./master_version_repo/version'

                    current_version_name = current_version_list[0].split(':')[0]
                    current_version_number = current_version_list[0].split(':')[1].toDouble()
                    current_version_prod = current_version_list[3].split(':')[0]
                    current_version_prod_number = current_version_list[3].split(':')[1].toDouble()

                    master_version_name = master_version_list[0].split(':')[0]
                    master_version_number = master_version_list[0].split(':')[1].toDouble()
                    master_version_prod = master_version_list[3].split(':')[0]
                    master_version_prod_number = master_version_list[3].split(':')[1].toDouble()

                    if (master_version_name != 'version' || master_version_prod != 'prod' ||
                            current_version_name != 'version' || current_version_prod != 'prod')
                    {
                        error "Please check your version file key name or index!"
                    }

                    if (current_version_number < master_version_number){
                        error "Please check your version number!"
                    }
                    else if(current_version_number > master_version_number){
                        if (master_version_number != master_version_prod_number){
                            error "master_version_number != master_version_prod_number"
                        }
                    }

                }
            }
        }
    }
}
