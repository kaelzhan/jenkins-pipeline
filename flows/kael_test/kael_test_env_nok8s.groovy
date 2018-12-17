@Library('my_lib') pipelineLibrary

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.Secret;
import java.util.Arrays;
import java.util.Collections;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

pipeline {
    agent any

    parameters {
        string(name: 'client_id', defaultValue: '500000001', description: 'clinet_id')
        string(name: 'env', defaultValue: 'playground', description: 'environment')
        string(name: 'JENKINS_EXECUTION_ID', defaultValue: 'xxxxxxx', description: 'job id')
    }

    stages {
        stage('kael test step 1') {
            steps {
                script{
                    def creds = CredentialsMatchers.firstOrNull(
                        CredentialsProvider.lookupCredentials(StringCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, Collections.<DomainRequirement>emptyList()),
                        CredentialsMatchers.withId("jenkins")
                    )
                    print creds
                }

                /*script{
                    captain_notifier.captain_callback_onstart()
                }

                sh 'env'
                script{
                    learn_s3_helper.parameter_save()
                }
                script{
                    for (p in env.getEnvironment()) print p
                    for (p in params) print p
                    for (p in currentBuild) print p
                    learn_s3_helper.load_env_from_file(env.WORKSPACE+"/flows/kael_test/us-east-1-playground-fleet01.properties")
                    learn_s3_helper.env_set("env","test")
                    learn_s3_helper.env_set("host_name","blackboard.com")
                    captain_notifier.captain_callback_onfinish("SUCCESS")
                }*/

            }
        }
    }
}

