#!/usr/bin/env bash
# setup.sh — Student onboarding script for TaskFlow
# Run this once after cloning the repository.
#
# What it does:
#   1. Checks that Java 21+ is available (installs via apt if not)
#   2. Checks that Git is available (installs via apt/brew if not)
#   3. Checks that Maven 3.x is available (installs via apt if not)
#   4. Runs an initial build (compile + test) so you can see the current state
#   5. Prints a getting-started summary
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
  # java --version (Java 9+) writes to stdout:  openjdk 25 2025-09-16
  # java -version  (all)    writes to stderr:  openjdk version "21.0.3" ...
  JAVA_VER=$(java --version 2>/dev/null | head -1 | awk '{print $2}' | grep -oE '^[0-9]+')
  if [[ -z "$JAVA_VER" ]]; then
    # Fallback for older JDKs / unusual distributions
    JAVA_VER=$(java -version 2>&1 | head -1 | grep -oE '"[0-9]+' | grep -oE '[0-9]+')
  fi
  if [[ -n "$JAVA_VER" && "$JAVA_VER" -ge 21 ]]; then
    success "Java $JAVA_VER found."
  elif [[ -n "$JAVA_VER" ]]; then
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
    die "Please install Java 21 or later manually (https://adoptium.net) and re-run this script."
  fi
fi

###############################################################################
# 2. Check / install Git
###############################################################################
info "Checking Git..."
if command -v git &>/dev/null; then
  GIT_VER=$(git --version | awk '{print $3}')
  success "Git $GIT_VER found."
else
  warn "Git not found. Attempting to install..."
  if command -v apt-get &>/dev/null; then
    sudo apt-get update -qq
    sudo apt-get install -y git
  elif command -v brew &>/dev/null; then
    brew install git
  else
    die "Please install Git from https://git-scm.com/downloads and re-run this script."
  fi
fi

###############################################################################
# 3. Check / install Maven 3.x  (falls back to bundled wrapper ./mvnw)
###############################################################################
info "Checking Maven..."
if command -v mvn &>/dev/null; then
  MVN_VER=$(mvn --version 2>&1 | awk '/Apache Maven/ {print $3}')
  success "Maven $MVN_VER found."
  MVN="mvn"
elif [[ -x "$REPO_ROOT/mvnw" ]]; then
  warn "mvn not on PATH — using bundled Maven Wrapper (./mvnw)."
  MVN="$REPO_ROOT/mvnw"
else
  warn "Maven not found. Attempting to install..."
  if command -v apt-get &>/dev/null; then
    sudo apt-get update -qq
    sudo apt-get install -y maven
    MVN="mvn"
  else
    die "Maven not found and no wrapper present. Install Maven 3.x from https://maven.apache.org/download.cgi"
  fi
fi

###############################################################################
# 4. Initial build — compile only (no tests) to confirm the project builds
###############################################################################
info "Running initial compile..."
cd "$REPO_ROOT"
if "$MVN" compile -q; then
  success "Project compiled successfully."
else
  warn "Compilation failed. Some bugs in this project may prevent a clean compile."
  warn "That is expected — fixing those bugs is part of the exercise."
fi

###############################################################################
# 5. Run the full test suite to show the baseline (many tests will fail)
###############################################################################
info "Running test suite to show your starting baseline..."
info "(Many tests will FAIL — that is intentional. Your job is to fix the bugs.)"
echo ""
"$MVN" test 2>&1 | tail -30 || true   # don't exit on test failure

###############################################################################
# 6. Getting-started instructions
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
echo "    $MVN test              # run the full test suite"
echo "    $MVN compile           # quick compile check"
echo ""
echo "  Rules:"
echo "    - Fix only the bug described in your issue"
echo "    - Do not modify test files"
echo "    - One commit per issue is preferred"
echo ""
echo "======================================================================"
