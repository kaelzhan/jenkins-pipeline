#!/usr/bin/env groovy

def create_captain_callback_file(){
    def captain_callback_file = Pipeline_Parameters.captain_callback_file_name
    def captain_json = [: ]



    for( p in params ) {
        parameter_json.(p.key.toString()) = (p.value.toString())
        env.(p.key.toString()) = (p.value.toString())
    }
    parameter_json = readJSON text: groovy.json.JsonOutput.toJson(parameter_json)
    writeJSON(file: parameter_file_local, json: parameter_json, pretty: 4)
}