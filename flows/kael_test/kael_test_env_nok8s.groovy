@Library('my_lib') pipelineLibrary

import jenkins.model.Jenkins

pipeline {
    /*triggers {
        issueCommentTrigger('build4mepls')
        token: 'build4mepls'
    }*/
    agent any

    parameters {
        string(name: 'client_id', defaultValue: '500000001', description: 'clinet_id')
        string(name: 'env', defaultValue: 'playground', description: 'environment')
        string(name: 'JENKINS_EXECUTION_ID', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('kael test step 1') {
            steps {
                /*withContext{
                    token: 'build4mepls'
                }
                configure { Node project ->
                    project / authToken("myToken")
                }
                authenticationToken('secret')*/


                script{
                    Jenkins j = Jenkins.instance
                    if(!j.isQuietingDown()) {
                        def job_dsl_security = j.getExtensionList('javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration')[0]
                        if(job_dsl_security.useScriptSecurity) {
                            job_dsl_security.useScriptSecurity = false
                            println 'Job DSL script security has changed.  It is now disabled.'
                            job_dsl_security.save()
                            j.save()
                        }
                        else {
                            println 'Nothing changed.  Job DSL script security already disabled.'
                        }
                    }
                    else {
                        println 'Shutdown mode enabled.  Configure Job DSL script security SKIPPED.'
                    }
                }
                
                jobDsl scriptText: '''authenticationToken(\'build4mepl\')
                notifications {
                	endpoint(\'http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback\') {
                		event(\'all\')
                		timeout(10000)
                      	logLines(1)
                    }
                }'''

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

