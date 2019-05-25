import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.jobs.MultibranchWorkflowJob

jobList = [
    [
        name:"bio-computer",
        description:"bio computer job with R&Java.",
        gitOwner:"kaelzhan",
        gitRepo:"jenkins-pipeline",
        jenkinsfile_path:"workflow/bio_computer/jenkinsfile_bio_computer.groovy",
        include_branch:"master",
        folder:"sherry"
    ]
]

jobList.each{job_i->
    String name = job_i.name
        String job_description =job_i.description
        String gitOwner = job_i.gitOwner
        String gitRepo = job_i.gitRepo
        String jenkinsfile_path = job_i.jenkinsfile_path
        String git_credentialsId ='jenkins-git'
        String includes_branch = job_i.includes_branch
        String excludes_branch = job_i.excludes_branch
        String job_folder = job_i.folder

        multibranchPipelineJob(job_folder+'/'+name) {
            description(job_description)
            branchSources {
                git {
                    remote('git@github.com:/'+gitOwner+'/'+gitRepo+'.git')
                    credentialsId(git_credentialsId)
                    includes(includes_branch)
                    excludes(excludes_branch)
                }
            }

            configure { node ->
                node / sources / data / 'jenkins.branch.BranchSource'  / buildStrategies {
                    'jenkins.branch.buildstrategies.basic.NamedBranchBuildStrategyImpl' {
                        filters {
                            'jenkins.branch.buildstrategies.basic.NamedBranchBuildStrategyImpl_-WildcardsNameFilter' {
                                excludes( '*' )
                                includes( '' )
                                caseSensitive( false )
                            }
                        }
                    }
                }
            }

            configure { node ->
                node / sources / data / 'jenkins.branch.BranchSource' / source / traits {
                    'jenkins.plugins.git.traits.PruneStaleBranchTrait'()
                    'jenkins.plugins.git.traits.BranchDiscoveryTrait'()
                    'jenkins.plugins.git.traits.IgnoreOnPushNotificationTrait'()
                    'jenkins.scm.impl.trait.WildcardSCMHeadFilterTrait' {
                        excludes( excludes_branch )
                        includes( includes_branch )
                    }
                }
            }

            factory {
                workflowBranchProjectFactory {
                    scriptPath(jenkinsfile_path)
                }
            }

            orphanedItemStrategy {
                discardOldItems {
                    numToKeep(200)
                }
            }
        }
    }

