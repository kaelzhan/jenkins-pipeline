@Library('my_lib') pipelineLibrary

pipeline {
    agent any

    parameters {
        string(name: 'client_id', defaultValue: '500000001', description: 'clinet_id')
        string(name: 'env', defaultValue: 'playground', description: 'environment')
        string(name: 'JENKINS_EXECUTION_ID', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('kael test step 1') {
            steps {
                /*
                jobDsl scriptText: '''
                multibranchPipelineJob('kael_test_env_nok8s') {
                    notifications {
                        endpoint(\'http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback\') {
                            event(\'all\')
                            timeout(10000)
                            logLines(1)
                        }
                    }
                    authenticationToken("build4mepl")
                }
                '''
                */
                script{
                    captain_notifier.parameter_save()
                }

                sh 'env'
                script{
                    learn_s3_helper.parameter_save()
                }
                script{
                    for (p in env.getEnvironment()) print p
                    for (p in params) print p
                    for (p in currentBuild) print p
                    learn_s3_helper.load_env_from_file(env.WORKSPACE+"/flows/kael_test/us-east-1-playground-fleet01.properties")
                    learn_s3_helper.env_set("env","test")
                    learn_s3_helper.env_set("host_name","blackboard.com")
                }
            }
        }
    }
}

