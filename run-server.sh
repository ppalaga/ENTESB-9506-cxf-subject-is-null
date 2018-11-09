#!/bin/bash
#set -x
set -e

# Change these
eapZipPath=/home/data/servers/jboss-eap-7.1.0.zip
fuseInstallerPath701=/home/data/servers/fuse-eap-installer-7.0.1.fuse-000008-redhat-3.jar

pwDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
lab=${pwDir}/lab
fuseInstallerPath=${fuseInstallerPath701}

# build the quickstart
cd ${pwDir}/src/example-camel-cxf-jaxws
mvn clean package

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

mv ${lab}/wfc/standalone/configuration/standalone.xml ${lab}/wfc/standalone/configuration/standalone-orig.xml
cp -t ${lab}/wfc/standalone/configuration ${pwDir}/src/atmis-roles.properties ${pwDir}/src/atmis-users.properties ${pwDir}/src/standalone.xml
cp -t ${lab}/wfc/standalone/deployments ${pwDir}/src/example-camel-cxf-jaxws/target/example-camel.war

${lab}/wfc/bin/add-user.sh -a -u testUser -p testPassword1+ -g testRole

${lab}/wfc/bin/standalone.sh
