#!/usr/bin/env groovy

import java.net.*;

def callback_captain() {
    echo "test"

    def targetUrl = new URL("http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback")
    HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection()
    connection.setRequestProperty("Content-Type", String.format( "application/%s;charset=UTF-8", isJson ? "json" : "xml" ))
    connection.setConnectTimeout(10000)
    String userInfo = targetUrl.getUserInfo()
    if (null != userInfo) {
        String b64UserInfo = DatatypeConverter.printBase64Binary(userInfo.getBytes())
        String authorizationHeader = "Basic " + b64UserInfo
        connection.setRequestProperty("Authorization", authorizationHeader)
    }
    connection.setFixedLengthStreamingMode(data.length)
    connection.setDoInput(true)
    connection.setDoOutput(true)
    connection.setConnectTimeout(timeout)
    connection.setReadTimeout(timeout);
    connection.connect()
    try {
        OutputStream output = connection.getOutputStream()
        try {
            output.write(data)
            output.flush()
        } finally {
            output.close()
        }
    } finally {
        // Follow an HTTP Temporary Redirect if we get one,
        // NB: Normally using the HttpURLConnection interface, we'd call
        // connection.setInstanceFollowRedirects(true) to enable 307 redirect following but
        // since we have the connection in streaming mode this does not work and we instead
        // re-direct manually.
        if (307 == connection.getResponseCode()) {
            String location = connection.getHeaderField("Location")
            connection.disconnect()
            send(location, data,timeout, isJson)
        } else {
            connection.disconnect()
        }
    }






    context.with {
        authenticationToken('build4mepls')
    }
    context.with {
        notifications {
            endpoint('http://jenkins:lko34kd9fd2@captain.bbpd.io/api/jenkins/callback') {
                event('all')
                timeout(10000)
                logLines(1)
            }
        }
    }
}
