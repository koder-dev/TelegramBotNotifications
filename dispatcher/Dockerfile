FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p "$HOME"

COPY ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p "$HOME"/common
COPY ./common "$HOME"/common
RUN mvn -f "$HOME"/common/pom.xml -s "$MVN_SETTINGS" clean package
RUN mvn org.apache.maven.plugins:maven-install-plugin:3.1.2:install-file -Dfile="$HOME"/common/target/common-1.0.0.jar --debug

RUN mkdir -p "$HOME"/dispatcher
COPY ./dispatcher/pom.xml $HOME/dispatcher
COPY ./dispatcher/src "$HOME"/dispatcher/src
RUN mvn -f "$HOME"/dispatcher/pom.xml -s "$MVN_SETTINGS" clean package --debug

CMD java -Duser.timezone="Europe/Kiev" -jar "$HOME"/dispatcher/target/dispatcher-0.0.1.jar