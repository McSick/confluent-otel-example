FROM gitpod/workspace-full
ARG CONFLUENT_VERSION=6.2.1
ARG CONFLUENT_VERSION_SHORT=6.2
ENV CONFLUENT_HOME=/home/gitpod/confluent-${CONFLUENT_VERSION}
SHELL ["/bin/bash", "-c"] 

# Install Confluent CLI and Confluent Cloud CLI, with shell auto completion
RUN mkdir -p ~/.local/share/bash-completion/ && \
    echo "export PATH=/home/gitpod/.sdkman/candidates/java/current/bin:/home/gitpod/confluent-${CONFLUENT_VERSION}/bin:$PATH" >> ~/.bashrc
RUN curl -O https://packages.confluent.io/archive/${CONFLUENT_VERSION_SHORT}/confluent-${CONFLUENT_VERSION}.zip && \
    unzip confluent-${CONFLUENT_VERSION}.zip && \
    echo "source ~/.local/share/bash-completion/confluent" >> ~/.bashrc && \
    /home/gitpod/confluent-${CONFLUENT_VERSION}/bin/confluent completion bash > ~/.local/share/bash-completion/confluent
RUN curl -L --http1.1 https://cnfl.io/ccloud-cli | sudo sh -s -- -b /usr/local/bin && \
    touch ~/.local/share/bash-completion/ccloud && \
    ccloud completion bash > ~/.local/share/bash-completion/ccloud && \
    touch ~/.local/share/bash-completion/confluent && \
    echo "source ~/.local/share/bash-completion/ccloud" >> ~/.bashrc

RUN curl -Lo https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.1.0/opentelemetry-javaagent-all.jar > ~/opentelemetry-javaagent-all.jar
ENV PATH=/home/gitpod/.sdkman/candidates/java/current/bin:/home/gitpod/confluent-${CONFLUENT_VERSION}/bin:$PATH
RUN echo $PATH
# Install datagen connector
RUN /home/gitpod/confluent-${CONFLUENT_VERSION}/bin/confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.5.2