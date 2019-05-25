@Library('kael_test_jenkins') pipelineLibrary
import env_config.JenkinsCredentials


def container_Template = libraryResource 'com/kubernetes/kael_k8s_bio_computer_template.yaml'
def jenkins_git_id = JenkinsCredentials.jenkins_git_id

pipeline {
    agent {
        kubernetes {
            label 'bio_computer_' + UUID.randomUUID()
            defaultContainer 'kael-k8s-bio-computer'
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
                sh 'env'
                echo 'step 1'
            }
        }
        stage('kael test step 2') {
            steps {
                sh 'env'
                echo 'step 2'
            }
        }
    }
}

