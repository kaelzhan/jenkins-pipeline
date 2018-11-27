@Library('test-cj') pipelineLibrary
def container_Template = libraryResource 'com/k8s/containerTemplate.yaml'

pipeline {
    agent {
        kubernetes {
            label 'mypod'
            defaultContainer 'jnlp'
            yaml "${container_Template}"
        }
    }
//    parameters {
//        string(name: 'job_id', defaultValue: 'xxxxxxx', description: 'job id')
//    }

    stages {
        stage('Upload file') {
            steps {
//                script {
                    withAWS(credentials:'aws-pd-dev', region:'us-east-1') {
//                        s3Download(file: '/tmp/test.py', bucket: 'test', path: '/home/ubuntu/')
                        s3Upload(file:'/tmp/test.py', bucket:'my-bucket', path:'test/file.txt')
                    }
//                }
            }
        }
    }
}