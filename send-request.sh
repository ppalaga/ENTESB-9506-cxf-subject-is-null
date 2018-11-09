#!/bin/bash
#set -x
set -e

pwDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

curl -X POST -H "Content-Type: text/xml;charset=UTF-8" \
    -H "SOAPAction: http://example.com/services/helloworld/sayHello" \
    --data @${pwDir}/src/payload.xml \
    http://localhost:8080/webservices/helloworld
