#!/bin/bash
# Add all files
git add .

# Commit everything in one clean commit
git commit -m "Initial commit: full webpage"

# Rename branch to main
git branch -M main

git remote set-url origin https://github.com/AndronikosGl/PasswordScanner.git
# Add remote
git remote add origin https://github.com/AndronikosGl/PasswordScanner.git

# Force push to GitHub (this overwrites everything there)
git push -u origin main --force
