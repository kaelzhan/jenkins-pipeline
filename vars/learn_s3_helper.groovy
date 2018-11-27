#!/usr/bin/env groovy

import env_config.S3_Config
import env_config.Pipeline_Parameters
import groovy.json.JsonOutput

def format_s3_path(jobname, jobid) {
    def bucket_name = S3_Config.s3_bucket_name
    def s3_path = "s3://${bucket_name}/${jobname}/${jobid}"
    return s3_path
}

// download file from s3
def s3_read(jobname, jobid, filename) {
    def region = S3_Config.s3_region
    def s3_path = format_s3_path(jobname, jobid)
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: S3_Config.aws_credential_id]]) {
        withEnv(["region=${region}", "s3_path=${s3_path}", "filename=${filename}"]){
            try {
                sh 'aws s3 cp ${s3_path}/${filename} . --region ${region}'
            }
            catch (Exception err){
                echo "File ${filename} is not exist on s3 path ${s3_path}, return false."
                return false
            }
            return true
        }
    }
}

// upload file to s3
def s3_write(jobname, jobid, filename) {
    def region = S3_Config.s3_region
    def s3_path = format_s3_path(jobname, jobid)
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: S3_Config.aws_credential_id]]) {
        withEnv(["region=${region}", "s3_path=${s3_path}", "filename=${filename}"]){
            sh 'aws s3 cp ${filename} ${s3_path}/ --region ${region}'
        }
    }
}

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
    writeJSON(file: parameter_file_local, json: parameter_json)
}

// load all envs from _file to local file and runtime_env
def load_env_from_file(_file){
    def parameter_file = Pipeline_Parameters.environment_parameters_file_name
    def parameter_file_local = env.WORKSPACE + "/" + parameter_file
    def parameter_json = [: ]

    def props = readProperties interpolate: true, file: _file
    props.each {
        parameter_json.(it.key.toString()) = (it.value.toString())
        env.(it.key.toString()) = (it.value.toString())
    }
    parameter_json = readJSON text: groovy.json.JsonOutput.toJson(parameter_json)
    writeJSON(file: parameter_file_local, json: parameter_json)
}

// read all envs from s3 to local file and runtime_env
def env_read(){
    def parameter_file = Pipeline_Parameters.environment_parameters_file_name
    def parameter_file_local = env.WORKSPACE + "/" + parameter_file

    s3_read(env.JOB_NAME, params.JENKINS_EXECUTION_ID, parameter_file)
    withEnv(["parameter_file_local=${parameter_file_local}"]){
        sh 'chmod 777 ${parameter_file_local}'
    }
    parameter_json = readJSON file: parameter_file_local

    for ( p in parameter_json ){
        env.(p.key.toString()) = (p.value.toString())
    }
}

// set env with _key, _value and save it to local file
def env_set(_key,_value){
    def parameter_file = Pipeline_Parameters.environment_parameters_file_name
    def parameter_file_local = env.WORKSPACE + "/" + parameter_file
    def parameter_json = readJSON file: parameter_file_local

    parameter_json.(_key.toString()) = (_value.toString())
    env.(_key.toString()) = (_value.toString())
    writeJSON(file: parameter_file_local, json: parameter_json)
}

// save all envs from runtime_env to local file and s3
def env_save(){
    def parameter_file = Pipeline_Parameters.environment_parameters_file_name
    def parameter_file_local = env.WORKSPACE + "/" + parameter_file
    def parameter_json = [: ]

    for ( p in env.getEnvironment() ){
        parameter_json.(p.key.toString()) = (p.value.toString())
    }
    parameter_json = readJSON text: groovy.json.JsonOutput.toJson(parameter_json)
    writeJSON(file: parameter_file_local, json: parameter_json)
    s3_write(env.JOB_NAME, params.JENKINS_EXECUTION_ID, parameter_file)
}
