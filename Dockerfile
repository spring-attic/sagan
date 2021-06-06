FROM openjdk:8-jre

MAINTAINER AkshayGirpunje

# Update and install required packages
RUN apt-get update && \
    apt-get install -y curl iproute2 && \
    apt-get update && \
    apt-get clean

# Create required dirs
RUN mkdir -p /usr/local/sagan

# Set work dir
WORKDIR /usr/local/

COPY sagan /usr/local/sagan
COPY tools.jar /usr/local/openjdk-8/lib/tools.jar

RUN ln -sf /usr/lib/jvm/java-8-openjdk-amd64 /usr/local/openjdk-8

WORKDIR /usr/local/sagan

# Add required files
ADD entrypoint.sh ./



EXPOSE 8080

# Start the modules
ENTRYPOINT ["/bin/bash", "./entrypoint.sh"]
