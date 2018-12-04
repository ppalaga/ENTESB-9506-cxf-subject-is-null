#!/bin/bash
#set -x
set -e

# Change these
eapZipPath=/home/data/servers/jboss-eap-7.1.0.zip
fuseInstallerPath701=/home/data/servers/fuse-eap-installer-7.0.1.fuse-000008-redhat-3.jar
# Alternatively, you can run against WildFlyCamel
wildFlyCamelDistDir=/home/ppalaga/orgs/camel/wildfly-camel/itests/standalone/basic/target/wildfly-15.0.0.Final
# See below to choose WildFlyCamel or FUSE@EAP

export JAVA_HOME=/home/data/jvm/ora/jdk1.8.0_151

pwDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
lab=${pwDir}/lab
fuseInstallerPath=${fuseInstallerPath701}

# build the quickstart
cd ${pwDir}/src/example-camel-cxf-jaxws
mvn clean package

function reinstallFuse() {
    # clean the lab
    rm -Rf ${lab}
    mkdir -p ${lab}

    # unzip EAP
    cd ${lab}
    unzip -qq ${eapZipPath}
    mv ${lab}/jboss-eap-7.1 ${lab}/wfc

    # install Fuse
    cd ${lab}/wfc
    java -jar ${fuseInstallerPath}
}

function reinstallWildFlyCamel() {
    # clean the lab
    rm -Rf ${lab}
    mkdir -p ${lab}

    cp -r -t ${lab} "${wildFlyCamelDistDir}"
    mv ${lab}/wildfly-15.0.0.Final ${lab}/wfc

    #uncomment if you need to debug
    #echo 'JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y"' >> ${lab}/wfc/bin/standalone.conf

}

# Choose one
reinstallFuse
#reinstallWildFlyCamel

mv ${lab}/wfc/standalone/configuration/standalone.xml ${lab}/wfc/standalone/configuration/standalone-orig.xml
cp -t ${lab}/wfc/standalone/configuration ${pwDir}/src/atmis-roles.properties ${pwDir}/src/atmis-users.properties ${pwDir}/src/standalone.xml
cp -t ${lab}/wfc/standalone/deployments ${pwDir}/src/example-camel-cxf-jaxws/target/example-camel.war

${lab}/wfc/bin/standalone.sh
