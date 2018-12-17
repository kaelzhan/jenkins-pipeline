import env_config.Pipeline_Parameters
import groovy.json.JsonOutput
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.security.ACL;
import jenkins.model.Jenkins;

// Get password with _username in jenkins
def get_password(_username){
    def allCreds = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, Collections.<DomainRequirement>emptyList())
    def cred = CredentialsMatchers.firstOrNull(allCreds, CredentialsMatchers.withUsername(_username))
    return cred.getPassword()
}

// Create a json string file which includes the attributes that should be sent back to captain.
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
    writeJSON(file: captain_callback_file, json: captain_json)
}

// Call back to captain when a jenkins job started.
def captain_callback_onstart(){

    echo "--0-start-create-captain-call-file-------"
    create_captain_call_file()
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name

    echo "--3-start-get-captain-user-cred-------"

    def captain_callback_user = Pipeline_Parameters.captain_callback_user_name
    def captain_callback_cred = get_password(captain_callback_user)

    echo "--4-start-post-notification-to-captain-------"
    withEnv(["captain_callback_file=${captain_callback_file}", "captain_callback_user=${captain_callback_user}", "captain_callback_cred=${captain_callback_cred}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -d @${captain_callback_file} http://${captain_callback_user}:${captain_callback_cred}@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}

// Call back to captain when a jenkins job ended. The job_result should be "SUCCESS" or "FAILURE".
def captain_callback_onfinish(job_result){
    def captain_callback_file = env.WORKSPACE + "/" + Pipeline_Parameters.captain_callback_file_name
    def captain_callback_user = Pipeline_Parameters.captain_callback_user_name
    def captain_callback_cred = get_password(captain_callback_user)
    def captain_json = readJSON file: captain_callback_file

    captain_json.build.phase = "FINALIZED"
    captain_json.build.status = job_result
    writeFile file: captain_callback_file, text: captain_json

    withEnv(["captain_callback_file=${captain_callback_file}", "captain_callback_user=${captain_callback_user}", "captain_callback_cred=${captain_callback_cred}"]){
        sh '''#!/bin/bash
        set +e
        curl --max-time 60 --insecure -k -f -H "Content-Type:application/json;charset=UTF-8" -X POST -d @${captain_callback_file} http://${captain_callback_user}:${captain_callback_cred}@captain.bbpd.io/api/jenkins/callback
        if (( $? != 0 )); then
          echo "WARNING: Could not post to captain - see output above"
        fi
        '''
    }
}
