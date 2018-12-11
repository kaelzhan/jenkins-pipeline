#!/usr/bin/env groovy

def captain_call(){
    jobDsl scriptText: '''
    pipelineJob('kael_test_env_nok8s/job/master') {
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
}