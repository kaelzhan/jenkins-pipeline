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
        booleanParam(name: 'singel_seq', defaultValue: true, description: '单端测序，true or false?')
    }

    stages {
        stage('hisat2') {
            steps {
                sh '''#!/bin/bash
source ~/.bashrc
fullinputpath="/opt/work/"$inputpath
cd $fullinputpath
if [ singel_seq ]
    for i in `ls`
    do
        hisat2 -p 8 -t -x /opt/work/resources/dmel -U $i  -S /opt/work/output/$i.sam
    done
else
    for i in `seq -w 01 $[$(ls|wc -l)/2]`
    do
        echo $i
        m1=$(ls *$i*.fq.gz|awk '{print $1}')
        m2=$(ls *$i*.fq.gz|awk '{print $2}')
        hisat2 -p 8 -t -x /opt/work/resources/dmel -1 $m1 -2 $m2 -S /opt/work/output/$m1.sam
    done
fi
'''
            }
        stage('samtools') {
            steps {
                echo 'step 2'
            }
        }
        stage('htseq') {
            steps {
                echo 'step 3'
            }
        }
        stage('read_counts') {
            steps {
                echo 'step 4'
            }
        }
        stage('R_map') {
            steps {
                echo 'step 5'
            }
        }
    }
}

