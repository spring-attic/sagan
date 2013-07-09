git pull --rebase
mvn clean package -Dmaven.test.skip=true
mvn sass:watch &
java -jar target/springframework-site-?.?.*.jar
