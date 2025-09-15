#!/bin/bash

clear

cd /home/andronikos/NetBeansProjects/PasswordScanner/ || exit

# Compile Java files into build/classes
javac -d build/classes src/PasswordScanner/*.java

# Run the program (note: package name required!)
java \
  -Dsun.java2d.uiScale=2 \
  -cp build/classes PasswordScanner.NewClass

# Wait for key press before closing
read -n 1 -s -r -p "Press any key to continue..."

