#!/bin/bash

JAR1='/home/bgshin/works/cs571/target/corenlp-1.0.0-SNAPSHOT.jar'
JAR2='/home/bgshin/.m2/repository/edu/emory/mathcs/nlp/common/1.0.0-SNAPSHOT/common-1.0.0-SNAPSHOT.jar'
JAR3='/home/bgshin/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar'
# JAR4='/home/bgshin/.m2/repository/args4j/args4j/2.0.31/args4j-2.0.31.jar'
JAR4='/home/bgshin/.m2/repository/args4j/args4j/2.32/args4j-2.32.jar'
JAR5='/home/bgshin/.m2/repository/it/unimi/dsi/fastutil/7.0.7/fastutil-7.0.7.jar'
JAR6='/home/bgshin/.m2/repository/org/tukaani/xz/1.5/xz-1.5.jar'
JAR7='/home/bgshin/.m2/repository/org/apache/commons/commons-math3/3.5/commons-math3-3.5.jar'
JAR8='/home/bgshin/.m2/repository/org/magicwerk/brownies-collections/0.9.10/brownies-collections-0.9.10.jar'
#---------------------------------#
RUN='edu.emory.mathcs.nlp.bin.DEPTrain'
# ARG='-c src/main/resources/configuration/config_train_dep.xml -t src/main/resources/dat/wsj-dep/trn -d src/main/resources/dat/wsj-dep/dev -te dep -de dep'
ARG='-c /home/bgshin/works/cs571/src/main/resources/configuration/config_train_dep.xml -t /home/bgshin/works/cs571/src/main/resources/dat/wsj-dep/trn -d /home/bgshin/works/cs571/src/main/resources/dat/wsj-dep/dev -te dep -de dep'


# mvn clean install compile

echo '=====================[new features]============================='
javac -cp ./src/main/java/:.:$JAR7:$JAR1:$JAR2:$JAR3:$JAR4:$JAR5:$JAR6 ./src/main/java/edu/emory/mathcs/nlp/bin/DEPTrain.java
java -cp $JAR7:$JAR1:$JAR2:$JAR3:$JAR4:$JAR5:$JAR6:$JAR8 $RUN $ARG



