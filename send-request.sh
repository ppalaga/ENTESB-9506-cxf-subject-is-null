#!/bin/bash
#set -x
set -e

pwDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

url="http://localhost:8080/example-camel/HelloWorld_Service"
#url="http://localhost:8080/webservices/helloworld"

curl -X POST -H "Content-Type: text/xml;charset=UTF-8" \
    -H "SOAPAction: http://example.com/services/helloworld/sayHello" \
    --data @${pwDir}/src/payload.xml \
    ${url}
