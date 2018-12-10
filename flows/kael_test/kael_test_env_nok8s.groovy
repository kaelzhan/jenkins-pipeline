    authenticationToken('secret')
    batchTask('upload', 'curl --upload-file build/dist.zip http://www.example.com/upload')
    blockOnUpstreamProjects()
    blockOnDownstreamProjects()
    checkoutRetryCount(10)
    concurrentBuild()
    customWorkspace('/tmp')
    jdk('jdk-1.8')
    label('linux')
    lockableResources('lock-resource')
    multiscm {
         // ...
    }
    publishers {
         // ...
    }
    scm {
         // ...
    }
    steps {
         // ...
    }
    weight(50)
    wrappers {
         // ...
    }
