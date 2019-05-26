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
        string(name: 'path', defaultValue: 'input', description: 'The input files path under work.')
    }

    stages {
        stage('fastqc') {
            steps {
                sh 'env'
                sh '''
fullpath = "/opt/work/"$path
for i in ` ls $fullpath `
do
    fastqc $i > $i.fastqcout
done
'''
            }
        }
    }
}

