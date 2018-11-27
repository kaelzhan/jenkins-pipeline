
pipeline {
    agent any
//    parameters {
//        string(name: 'job_id', defaultValue: 'xxxxxxx', description: 'job id')
//    }

    stages {
        stage('Upload file') {
            steps {
                withAWS(credentials:'aws-playground', region:'us-east-1') {
                    //sh 'env'
                    //sh 'unset AWS_SESSION_TOKEN'
                    withEnv(["AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}", "AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}"]) {
                        sh 'unset AWS_SESSION_TOKEN'
                        sh 'env'
                        sh 'aws s3 ls'
                    }

//                        s3Download(file: '/tmp/test.py', bucket: 'test', path: '/home/ubuntu/')
                    s3Upload(pathStyleAccessEnabled: true, payloadSigningEnabled: true, file:'/tmp/test.py', bucket:'bb-test-pipeline', path:'file.txt')
                }
//                script {
                //withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-playground']]) {
                //sh 'env'
                //sh 'aws s3 ls'
                //}

//                }
            }
        }
    }
}