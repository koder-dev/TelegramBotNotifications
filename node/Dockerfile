FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p "$HOME"

COPY ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p "$HOME"/common
COPY ./common "$HOME"/common
RUN mvn -f "$HOME"/common/pom.xml -s "$MVN_SETTINGS" clean package
RUN mvn org.apache.maven.plugins:maven-install-plugin:3.1.2:install-file -Dfile="$HOME"/common/target/common-1.0.0.jar --debug

RUN mkdir -p "$HOME"/node
COPY ./node/pom.xml "$HOME"/node
COPY ./node/src "$HOME"/node/src
RUN mvn -f "$HOME"/node/pom.xml -s "$MVN_SETTINGS" clean package -DskipTests --debug

CMD java -jar "$HOME"/node/target/node-1.0.0.jar