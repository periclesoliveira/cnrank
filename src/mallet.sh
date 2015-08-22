echo "compiling code" 
javac CNIGen.java
echo "setup postgres jdbc" 
. /etc/profile
echo "executing mallet program" 
java  -Xms128m -Xmx1024m -XX:MaxPermSize=512m CNIGen
