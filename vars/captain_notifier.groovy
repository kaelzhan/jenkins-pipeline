#!/usr/bin/env groovy

import env_config.Pipeline_Parameters
import groovy.json.JsonOutput

/*
def create_captain_call_file(){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_json = [: ]

    def pipeline_jobname = "job/" + env.JOB_NAME.toString().split('/')[0] + "/job/" + env.JOB_NAME.toString().split('/')[1] + '/'
    captain_json.("name") = env.JOB_NAME.toString()
    captain_json.("url") = pipeline_jobname.toString()
    captain_json.("build") = [: ]
    captain_json.("build").("ful_url") = env.JOB_URL.toString()
    captain_json.("build").("number") = env.BUILD_ID.toString()
    captain_json.("build").("phase") = "STARTED"
    captain_json.("build").("status") = "SUCCESS"
    captain_json.("build").("parameters") = [: ]
    for (p in params){
        captain_json.(p.key.toString()) = (p.value.toString())
    }
    writeJSON(file: captain_callback_file, json: captain_json, pretty: 4)
}*/

// save all parameters from caption to local file and runtime_env
def parameter_save(){
    def parameter_file = Pipeline_Parameters.environment_parameters_file_name
    def parameter_file_local = env.WORKSPACE + "/" + parameter_file
    def parameter_json = [: ]

    for( p in params ) {
        parameter_json.(p.key.toString()) = (p.value.toString())
        env.(p.key.toString()) = (p.value.toString())
    }
    parameter_json = readJSON text: groovy.json.JsonOutput.toJson(parameter_json)
    writeJSON(file: parameter_file_local, json: parameter_json, pretty: 4)
}