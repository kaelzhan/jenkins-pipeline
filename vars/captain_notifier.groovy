#!/usr/bin/env groovy

import env_config.Pipeline_Parameters
import groovy.json.JsonOutput

def create_captain_call_file(){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_json = [: ]

    if( env.JOB_NAME.count("/") == 0 ){
        pipeline_jobname = "job/" + env.JOB_NAME + '/'
    }else{
        pipeline_jobname = "job/" + env.JOB_NAME.toString().split('/')[0] + "/job/" + env.JOB_NAME.toString().split('/')[1] + '/'
    }
    captain_json.("name") = env.JOB_NAME.toString()
    captain_json.("url") = pipeline_jobname.toString()
    captain_json.("build") = [: ]
    captain_json.("build").("url") = pipeline_jobname + env.BUILD_ID.toString() + "/"
    captain_json.("build").("ful_url") = env.JOB_URL.toString() + env.BUILD_ID.toString() + "/"
    captain_json.("build").("number") = env.BUILD_ID.toString()
    captain_json.("build").("phase") = "STARTED"
    captain_json.("build").("status") = "null"
    captain_json.("build").("parameters") = [: ]
    for (p in params){
        captain_json.("build").("parameters").(p.key.toString()) = (p.value.toString())
    }
    captain_json = readJSON text: groovy.json.JsonOutput.toJson(captain_json)
    writeJSON(file: captain_callback_file, json: captain_json, pretty: 4)
}

def captain_callback_onstart(){
    create_captain_call_file()
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    captain_json = readJSON file: captain_callback_file

    withEnv(["captain_callback_file=${captain_callback_file}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -F "file@${captain_callback_file}" http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}

def captain_callback_onfinish(job_result){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_json = readJSON file: captain_callback_file

    captain_json.("build").("phase") = "FINALIZED"
    captain_json.("build").("status") = job_result
    writeJSON(file: captain_callback_file, json: captain_json, pretty: 4)

    withEnv(["captain_callback_file=${captain_callback_file}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -F "file@${captain_callback_file}" http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}
