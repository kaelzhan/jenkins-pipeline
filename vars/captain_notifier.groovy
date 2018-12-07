#!/usr/bin/env groovy

def add_captain_callback(context) {
    context.with {
        authenticationToken('build4mepls')
    }
    context.with {
        notifications {
            endpoint('http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback') {
                event('all')
                timeout(10000)
                logLines(1)
            }
        }
    }
}
