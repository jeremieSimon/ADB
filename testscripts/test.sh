# TODO set hashbang?
# Don't forget to set exec permissions!

# Make these directories if they don't exsit yet
mkdir actual
mkdir error

# Compile java program
javac -classpath ../src -d ../bin ../src/edu/nyu/cs/adb/*.java

# Execute the program on these files
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest1.txt > actual/ADBPartIITest1.txt 2> error/ADBPartIITest1.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest2.txt > actual/ADBPartIITest2.txt 2> error/ADBPartIITest2.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest3.txt > actual/ADBPartIITest3.txt 2> error/ADBPartIITest3.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest4.txt > actual/ADBPartIITest4.txt 2> error/ADBPartIITest4.txt
#java -classpath ../bin edu.nyu.cs.adb.RepCRecApp < input/ADBPartIITest5.txt > actual/ADBPartIITest5.txt 2> error/ADBPartIITest5.txt

# New version of program
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest1.txt actual/ADBPartIITest1.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest2.txt actual/ADBPartIITest2.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest3.txt actual/ADBPartIITest3.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest4.txt actual/ADBPartIITest4.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest5.txt actual/ADBPartIITest5.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest6.txt actual/ADBPartIITest6.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest7.txt actual/ADBPartIITest7.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest8.txt actual/ADBPartIITest8.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest9.txt actual/ADBPartIITest9.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest10.txt actual/ADBPartIITest10.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest11.txt actual/ADBPartIITest11.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest12.txt actual/ADBPartIITest12.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest13.txt actual/ADBPartIITest13.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest14.txt actual/ADBPartIITest14.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest15.txt actual/ADBPartIITest15.txt
java -classpath ../bin edu.nyu.cs.adb.RepCRecApp input/ADBPartIITest31.txt actual/ADBPartIITest31.txt

# Compare expected output against actual results
diff expected actual > testresults
