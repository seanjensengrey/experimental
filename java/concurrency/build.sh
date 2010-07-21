#javac [class].java -d build.out/
#java -cp $PATH Gnoloo build.out/[class].class >  build.out/[class].j

# compile classes
javac src/Class1.java -d build.out/
javac src/Class2.java -d build.out/
javac src/Class3.java -d build.out/
javac src/Class4.java -d build.out/
javac src/Class5.java -d build.out/
javac src/Class6.java -d build.out/

# convert byte code
java -cp $PATH Gnoloo build.out/Class1.class > Class1.j
java -cp $PATH Gnoloo build.out/Class2.class > Class2.j
java -cp $PATH Gnoloo build.out/Class3.class > Class3.j
java -cp $PATH Gnoloo build.out/Class4.class > Class4.j
java -cp $PATH Gnoloo build.out/Class5.class > Class5.j
java -cp $PATH Gnoloo build.out/Class6.class > Class6.j
