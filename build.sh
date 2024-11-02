cd jMetal \
&& mvn package -DskipTests \
&& cd ../jgrapht \
&& mvn package -DskipTests \
&& cd .. \
&& mvn compile