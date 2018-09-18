#!/bin/bash
#
# MIT License
#
# Copyright 2017-2018 Sabre GLBL Inc.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
set -e

source ".travis/release.properties"

repositoryRoot=`git rev-parse --show-toplevel`

function perform_regular_build() {
    echo "Performing regular build..."

    ${repositoryRoot}/mvnw clean verify
}

function perform_release_build() {
    echo "Performing release '${releaseVersion}' ..."

    setup

    ${repositoryRoot}/mvnw release:clean release:prepare release:perform \
            --errors \
            --batch-mode \
            --settings .travis/release.settings.xml \
            -DdryRun=${_GIT_DRY_RUN} \
            -DautoReleaseAfterClose=${_OSSRH_AUTO_RELEASE} \
            -DreleaseVersion=${releaseVersion} \
            -DdevelopmentVersion=${developmentVersion}
}

function setup() {

    # *** DEFAULTS
    _GIT_DRY_RUN=${_GIT_DRY_RUN:-"false"}
    _OSSRH_AUTO_RELEASE=${_OSSRH_AUTO_RELEASE:-"true"}
    GPG_EXECUTABLE=${GPG_EXECUTABLE:-"gpg"}

    validate

    # *** GIT
    git config user.name "${GIT_CONFIG_USERNAME}"
    git config user.email "${GIT_CONFIG_EMAIL}"

    # changing file flag to executable should not be treated as a change
    git config --add core.filemode false

    # Travis CI works in detached mode so we have to fix it
    if [ "${_GIT_DRY_RUN}" == "false" ]; then
        git checkout ${TRAVIS_BRANCH}
        git reset --hard ${TRAVIS_COMMIT}
    fi

    # *** GPG
    set +e
    if [ ! -z "${GPG_SECRET_KEYS}" ]; then echo ${GPG_SECRET_KEYS} | base64 --decode | ${GPG_EXECUTABLE} --import; fi
    if [ ! -z "${GPG_OWNERTRUST}" ]; then echo ${GPG_OWNERTRUST} | base64 --decode | ${GPG_EXECUTABLE} --import-ownertrust; fi
    set -e

    # *** SSH AGENT
    # start the ssh-agent in the background if not already started
    # and add the private key used to commit to GitHub
    local state=$(ssh-add -l >| /dev/null 2>&1; echo $?)
    if ([ ! "${SSH_AUTH_SOCK}" ] || [ ${state} == 2 ]); then
        eval "$(ssh-agent -s)"
    fi
    echo "${SSH_SECRET_KEYS}" | base64 --decode | ssh-add -

    # *** DECODE BASE64 ENCODED ENV VARIABLES
    # due to the way how Travis sets the environment variables
    # most of variables are base64 encoded, so here we decode them
    OSSRH_PASSWORD=`echo ${OSSRH_PASSWORD} | base64 --decode`
    OSSRH_USER=`echo ${OSSRH_USER} | base64 --decode`
    GPG_PASSPHRASE=`echo ${GPG_PASSPHRASE} | base64 --decode`

    # export all the variables used by maven (see ./release.settings.xml)
    export OSSRH_PASSWORD
    export OSSRH_USER
    export GPG_EXECUTABLE
    export GPG_PASSPHRASE
    export GPG_KEY_NAME
}

function validate() {
    typeset -a variable_names=(
            releaseVersion developmentVersion
            OSSRH_USER OSSRH_PASSWORD
            GIT_CONFIG_USERNAME GIT_CONFIG_EMAIL
            SSH_SECRET_KEYS
            GPG_SECRET_KEYS GPG_OWNERTRUST GPG_PASSPHRASE GPG_KEY_NAME GPG_EXECUTABLE
            _GIT_DRY_RUN _OSSRH_AUTO_RELEASE )
    local code=0
    for variable_name in "${variable_names[@]}"; do
        value="$(eval "echo \${${variable_name}}")"
        if [ -z "${value}" ]; then
            echo "[ERROR] Missing or empty required environment variable ${variable_name}"
            code=1
        fi
    done
    if [ ${code} -ne 0 ]; then
        echo
        echo "Required environment variables:"
        echo "    releaseVersion          release version (see ./travis/release.properties file)"
        echo "    developmentVersion      new version after release (see ./travis/release.properties file)"
        echo "    OSSRH_USER              OSSRH username (authentication token recommended) - base64 encoded"
        echo "    OSSRH_PASSWORD          OSSRH password (authentication token recommended) - base64 encoded"
        echo "    GIT_CONFIG_USERNAME     username used to set git user.name setting (can be anything)"
        echo "    GIT_CONFIG_EMAIL        email used to set git user.email setting (can be anything)"
        echo "    SSH_SECRET_KEYS         SSH Private Key used to authenticate in git repo (i.e. GitHub) - base64 encoded"
        echo "    GPG_SECRET_KEYS         GPG Secret Key used to signing the artifacts - base64 encoded"
        echo "    GPG_OWNERTRUST          GPG Owner trust - base64 encoded"
        echo "    GPG_PASSPHRASE          GPG passphrase - base64 encoded"
        echo "    GPG_KEY_NAME            GPG key name"
        echo
        echo "Required environment variables with default values:"
        echo "    GPG_EXECUTABLE          gpg executable command. Default value is 'gpg'"
        echo "    _GIT_DRY_RUN            related to the release-maven-plugin:prepare \${dryRun} property. Default value is false"
        echo "    _OSSRH_AUTO_RELEASE     related to the nexus-staging-maven-plugin:deploy \${autoReleaseAfterClose}. Default value is true"
        exit ${code}
    fi
}

function build() {
    # Determine build type. If commit message matches "Release ${releaseVersion}"
    # then release build is performed, otherwise regular build.
    echo "Commit message: ${TRAVIS_COMMIT_MESSAGE}"
    echo "Travis jdk version: ${TRAVIS_JDK_VERSION}"
    local release_commit_message_pattern="^Release ${releaseVersion}$"
    if [[ "${TRAVIS_COMMIT_MESSAGE}" =~ ${release_commit_message_pattern} ]]; then
        if [[ "${TRAVIS_JDK_VERSION}" == "oraclejdk8" ]]; then
            perform_release_build
        else
            echo "Skipping release build for Travis jdk version '${TRAVIS_JDK_VERSION}', only 'oraclejdk8' is supported."
        fi
    else
        perform_regular_build
    fi
}

# Perform build
build


