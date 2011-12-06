# TODO set hashbang?
# Don't forget to set exec permissions!

# Make these directories if they don't exsit yet
mkdir actual
mkdir error

# Compile java program
javac -classpath ../src -d ../bin ../src/edu/nyu/cs/adb/*.java

# Execute the program on these files
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest1.txt 2> error/ADBPartIITest1.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest2.txt 2> error/ADBPartIITest2.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest3.txt 2> error/ADBPartIITest3.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest4.txt 2> error/ADBPartIITest4.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest5.txt 2> error/ADBPartIITest5.txt

# New version of program
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest1.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest2.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest3.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest4.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest5.txt

# Compare expected output against actual results
diff expected actual > testresults
