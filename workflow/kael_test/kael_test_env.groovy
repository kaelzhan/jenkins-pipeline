@Library('my_lib') pipelineLibrary

def container_Template = libraryResource 'com/kubernetes/learn_k8s_ruby_template.yaml'

pipeline {
    agent {
        kubernetes {
            label 'mypod'
            defaultContainer 'jnlp'
            yaml "${container_Template}"
        }
    }
    parameters {
        string(name: 'client_id', defaultValue: '500000001', description: 'clinet_id')
        string(name: 'env', defaultValue: 'playground', description: 'environment')
        string(name: 'JENKINS_EXECUTION_ID', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('kael test step 1') {
            steps {
                container('learn-k8s-ruby'){
                    sh 'env'
                    script{
                        learn_s3_helper.parameter_save()
                    }
                    sh 'env'
                    script{
                        learn_s3_helper.load_env_from_file(env.WORKSPACE+"/flows/kael_test/us-east-1-playground-fleet01.properties")
                        learn_s3_helper.env_set("env","test")
                        learn_s3_helper.env_set("host_name","blackboard.com")
                    }
                    sh 'env'
                    script{
                        learn_s3_helper.env_save()
                    }
                    sh 'env'
                }
            }
        }
        stage('kael test step 2') {
            steps {
                container('learn-k8s-ruby'){
                    sh 'env'
                    script{
                        learn_s3_helper.env_read()
                    }
                    sh 'env'
                    script{
                        learn_s3_helper.env_set("host_url","test.blackboard.com")
                        learn_s3_helper.env_set("host_name","blackboard.com.cn")
                    }
                    sh 'env'
                }
            }
        }
    }
}

