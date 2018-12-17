import env_config.Pipeline_Parameters
import groovy.json.JsonOutput

// Create a json string file which includes the attributes that should be sent back to captain.
def create_captain_call_file(){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_json = [:]

    pipeline_jobname = env.JOB_NAME.replaceAll( '/', '/job/' ) + '/'

    captain_json.name = env.JOB_NAME
    captain_json.display_name = env.JOB_NAME
    captain_json.url = pipeline_jobname

    captain_json.build = [
        url: pipeline_jobname + env.BUILD_ID + '/'
        full_url: env.JOB_URL + env.BUILD_ID + '/'
        number: env.BUILD_ID
        phase: 'STARTED'
        status: null
        parameters: [:]
    ]
    for (p in params){
        captain_json.build.parameters[ p.key.toString() ] = [ p.value.toString() ]
    }
    writeFile file: captain_callback_file, text: JsonOutput.toJson( captain_json )
}

// Call back to captain when a jenkins job started.
def captain_callback_onstart(){
    create_captain_call_file()
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name

    withEnv(["captain_callback_file=${captain_callback_file}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -d @${captain_callback_file} http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}

// Call back to captain when a jenkins job ended. The job_result should be "SUCCESS" or "FAILURE".
def captain_callback_onfinish(job_result){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_json = readJSON file: captain_callback_file

    captain_json.("build").("phase") = "FINALIZED"
    captain_json.("build").("status") = job_result
    writeJSON(file: captain_callback_file, json: captain_json)

    withEnv(["captain_callback_file=${captain_callback_file}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -d @${captain_callback_file} http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}
