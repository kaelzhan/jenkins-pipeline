@Library('test-cj') pipelineLibrary
def container_Template = libraryResource 'com/k8s/containerTemplate.yaml'

def generateStage(_job_id, _parametersMap) {
    return {
        stage("stage: ${_job_id}") {
            def _result = build job: 'test_mulit_jobs_child/master',
                parameters: [
                        [
                                $class: 'StringParameterValue',
                                name  : 'job_id',
                                value : _job_id,
                        ],
                        _parametersMap
                ],
                propagate: false
            echo "${_result.result}"
            if (_result.result != "SUCCESS") {
                throw (_result.result)
            }
        }
    }
}

pipeline {
    agent any

    parameters {
        text(name: 'job_ids', defaultValue: 'xxxxxxx', description: 'job id')
        text(name: 'other_parameters', defaultValue: '', description: 'other parameters')
    }

    stages {
        stage('Upload file') {
            steps {
                script {
                    echo "${params.other_parameters}"

                    def all_parameters = params.other_parameters.split('\n')

                    def all_jobs = params.job_ids.split('\n')

                    def parametersMap = all_parameters.collectEntries {
                        [$class: "${it.split(':')[0].trim()}",
                         name: it.split(':')[1].trim(),
                         value: it.split(':')[2].trim()]
                    }

                    def parallelStagesMap = all_jobs.collectEntries {
                        ["${it}" : generateStage(it, parametersMap)]
                    }


                    parallel parallelStagesMap
                }
            }
        }
    }
}