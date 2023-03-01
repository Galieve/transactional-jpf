FROM ubuntu:lunar

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive \
    apt-get -y install default-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /trans-jpf

ADD . /trans-jpf/

ENTRYPOINT [ "/bin/bash", "-l", "-c" ]