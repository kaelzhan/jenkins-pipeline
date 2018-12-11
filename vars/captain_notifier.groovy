#!/usr/bin/env groovy

def create_captain_call_file(){
    def captain_callback_file = Pipeline_Parameters.captain_callback_file_name
    def captain_json = [: ]

    def pipeline_jobname = "job/" + env.JOB_NAME.toString().split('/')[0] + "/job/" + env.JOB_NAME.toString().split('/')[1] + '/'
    captain_json.("name") = env.JOB_NAME.toString()
    captain_json.("url") = pipeline_jobname
    captain_json.("build") = [: ]
    captain_json.("build").("ful_url") = env.JOB_URL
    captain_json.("build").("number") = env.BUILD_ID
    captain_json.("build").("phase") = "STARTED"
    captain_json.("build").("status") = "SUCCESS"
    captain_json.("build").("parameters") = [: ]
    for (p in params){
        captain_json.("build").("parameters").(p.key.toString()) = (p.value.toString())
    }
    writeJSON(file: captain_callback_file, json: captain_json, pretty: 4)
}