pipeline {
    agent {
        kubernetes {
            label 'mypod'
            defaultContainer 'jnlp'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  volumes:
  - name: foo
    secret:
      secretName: my-chef-key
  containers:
  - name: maven
    image: maven:3.3.9-jdk-8-alpine
    command:
    - cat
    tty: true
    volumeMounts:
    - name: foo
      mountPath: "/root/.chef"
      readOnly: true
  - name: busybox
    image: busybox
    command:
    - cat
    tty: true
"""
        }
    }

    stages {
        stage('Stream job') {
            steps {
                echo 'pppppp'
            }
        }
        stage('Start K8s label') {
            steps {
                container('maven') {
                    sh 'ls -al ~/.chef'
                }
            }
        }
    }
}