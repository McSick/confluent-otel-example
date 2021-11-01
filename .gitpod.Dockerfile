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

# Downloading and installing Maven
# 1- Define a constant with the version of maven you want to install
ARG MAVEN_VERSION=3.6.1         

# 2- Define a constant with the working directory
ARG USER_HOME_DIR="~/.local/share/bash-completion/"

# 3- Define the SHA key to validate the maven download
ARG SHA=b4880fb7a3d81edd190a029440cdf17f308621af68475a4fe976296e71ff4a4b546dd6d8a58aaafba334d309cc11e638c52808a4b0e818fc0fd544226d952544

# 4- Define the URL where maven can be downloaded from
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

# 5- Create the directories, download maven, validate the download, install it, remove downloaded file and set links
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "Downlaoding maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "Checking download hash" \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  \
  && echo "Unziping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# 6- Define environmental variables required by Maven, like Maven_Home directory and where the maven repo is located
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.1.0/opentelemetry-javaagent-all.jar > ~/opentelemetry-javaagent-all.jar
ENV PATH=/home/gitpod/.sdkman/candidates/java/current/bin:/home/gitpod/confluent-${CONFLUENT_VERSION}/bin:$PATH
RUN echo $PATH
# Install datagen connector
RUN /home/gitpod/confluent-${CONFLUENT_VERSION}/bin/confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.5.2