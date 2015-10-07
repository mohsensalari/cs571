#!/bin/bash

JAR1='/home/bgshin/cs571/target/corenlp-1.0.0-SNAPSHOT.jar'
JAR2='/home/bgshin/.m2/repository/edu/emory/mathcs/nlp/common/1.0.0-SNAPSHOT/common-1.0.0-SNAPSHOT.jar'
JAR3='/home/bgshin/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar'
JAR4='/home/bgshin/.m2/repository/args4j/args4j/2.0.31/args4j-2.0.31.jar'
JAR5='/home/bgshin/.m2/repository/it/unimi/dsi/fastutil/7.0.7/fastutil-7.0.7.jar'
JAR6='/home/bgshin/.m2/repository/org/tukaani/xz/1.5/xz-1.5.jar'
#---------------------------------#
RUN='edu.emory.mathcs.nlp.bin.POSTrain'
ARG='-c /home/bgshin/cs571/src/main/resources/configuration/config_train_pos.xml -t /home/bgshin/Data/wsj/pos/trn -d /home/bgshin/Data/wsj/pos/dev -te pos -de pos'




batch=$(awk 'BEGIN{for(i=0.05;i<=1;i+=0.05)print i}') #20
for b in $batch
do
  echo '=====================[batch_ratio is :'$b']============================='
  pushd . &> /dev/null
  cd ~/cs571/src/main/resources/configuration/
  sed 's/<batch_ratio>0.1/<batch_ratio>'$b'/' config_train_pos.xml.bak > config_train_pos.xml
  popd  &> /dev/null
  java -cp $JAR1:$JAR2:$JAR3:$JAR4:$JAR5:$JAR6 $RUN $ARG
done




