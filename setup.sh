#!/usr/bin/env bash
# setup.sh — Student onboarding script for TaskFlow
# Run this once after cloning the repository.
#
# What it does:
#   1. Checks that Java 21+ and Maven 3.x are available (installs via apt if not)
#   2. Runs an initial build (compile + test) so you can see the current state
#   3. Prints a getting-started summary
#
# Usage:
#   bash setup.sh

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"

###############################################################################
# Helpers
###############################################################################
info()    { echo "[INFO]  $*"; }
success() { echo "[OK]    $*"; }
warn()    { echo "[WARN]  $*"; }
die()     { echo "[ERROR] $*" >&2; exit 1; }

###############################################################################
# 1. Check / install Java 21+
###############################################################################
info "Checking Java..."
if command -v java &>/dev/null; then
  JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
  if [[ "$JAVA_VER" -ge 21 ]]; then
    success "Java $JAVA_VER found: $(java -version 2>&1 | head -1)"
  else
    warn "Java $JAVA_VER found but project requires Java 21+. Attempting upgrade..."
    if command -v apt-get &>/dev/null; then
      sudo apt-get update -qq
      sudo apt-get install -y openjdk-21-jdk
    else
      die "Please install Java 21 manually and re-run this script."
    fi
  fi
else
  warn "Java not found. Attempting to install openjdk-21..."
  if command -v apt-get &>/dev/null; then
    sudo apt-get update -qq
    sudo apt-get install -y openjdk-21-jdk
  else
    die "Please install Java 21 manually and re-run this script."
  fi
fi

###############################################################################
# 2. Check / install Maven 3.x
###############################################################################
info "Checking Maven..."
if command -v mvn &>/dev/null; then
  MVN_VER=$(mvn --version 2>&1 | awk '/Apache Maven/ {print $3}')
  success "Maven $MVN_VER found."
else
  warn "Maven not found. Attempting to install..."
  if command -v apt-get &>/dev/null; then
    sudo apt-get update -qq
    sudo apt-get install -y maven
  else
    die "Please install Maven 3.x manually and re-run this script."
  fi
fi

###############################################################################
# 3. Initial build — compile only (no tests) to confirm the project builds
###############################################################################
info "Running initial compile..."
cd "$REPO_ROOT"
if mvn compile -q; then
  success "Project compiled successfully."
else
  warn "Compilation failed. Some bugs in this project may prevent a clean compile."
  warn "That is expected — fixing those bugs is part of the exercise."
fi

###############################################################################
# 4. Run the full test suite to show the baseline (many tests will fail)
###############################################################################
info "Running test suite to show your starting baseline..."
info "(Many tests will FAIL — that is intentional. Your job is to fix the bugs.)"
echo ""
mvn test 2>&1 | tail -30 || true   # don't exit on test failure

###############################################################################
# 5. Getting-started instructions
###############################################################################
echo ""
echo "======================================================================"
echo "  TaskFlow — Bug-fixing Exercise"
echo "======================================================================"
echo ""
echo "  Repository layout:"
echo "    src/main/java/     — application source (this is where the bugs are)"
echo "    src/test/java/     — automated tests    (do NOT modify these)"
echo "    issues/            — your assigned issue (e.g. issues/issue-001.md)"
echo ""
echo "  Workflow:"
echo "    1. Read the issue assigned to you (e.g. issues/issue-001.md)"
echo "    2. Find and fix the bug in src/main/java/"
echo "    3. Verify your fix:  ./check.sh <issue-number>"
echo "       Example:          ./check.sh 1"
echo "    4. Open a Pull Request — do NOT merge it yourself"
echo ""
echo "  Useful commands:"
echo "    ./check.sh 15          # run tests for issue #15"
echo "    mvn test               # run the full test suite"
echo "    mvn compile            # quick compile check"
echo ""
echo "  Rules:"
echo "    - Fix only the bug described in your issue"
echo "    - Do not modify test files"
echo "    - One commit per issue is preferred"
echo ""
echo "======================================================================"
