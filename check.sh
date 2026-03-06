#!/usr/bin/env bash
# check.sh — Run the tests for a specific issue and report PASS / FAIL.
#
# Usage:
#   ./check.sh <issue-number>
#   ./check.sh 15
#   ./check.sh 24
#
# The script maps each issue number to the test class(es) that cover it,
# runs only those tests via Maven, and exits 0 (PASS) or 1 (FAIL).

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"

###############################################################################
# Helpers
###############################################################################
usage() {
  echo "Usage: ./check.sh <issue-number>"
  echo "       ./check.sh 15"
  exit 1
}

pass() {
  echo ""
  echo "╔══════════════════════════════════════╗"
  echo "║  ✅  PASS — Issue #${1} fixed!         "
  echo "╚══════════════════════════════════════╝"
}

fail() {
  echo ""
  echo "╔══════════════════════════════════════╗"
  echo "║  ❌  FAIL — Issue #${1} not yet fixed  "
  echo "╚══════════════════════════════════════╝"
  echo ""
  echo "  Review the test output above, then fix the bug in src/main/java/"
  echo "  and re-run:  ./check.sh ${1}"
}

###############################################################################
# Argument validation
###############################################################################
[[ $# -ne 1 ]] && usage
ISSUE="$1"
# Strip leading zeros if the student types e.g. ./check.sh 01
ISSUE="${ISSUE#0}"
ISSUE="${ISSUE#0}"   # strip again for triple-digit leading zeros

###############################################################################
# Issue → test class mapping
#
# Format per line:   ISSUE_NUMBER) TEST_CLASSES ;;
# TEST_CLASSES is a comma-separated list of fully-qualified class names.
# Issues without automated tests are noted explicitly.
###############################################################################
case "$ISSUE" in
  1)  TEST_CLASSES="com.taskflow.controller.TaskControllerTest#testCreateTaskReturns201" ;;
  2)  TEST_CLASSES="com.taskflow.controller.TaskControllerTest#testGetTaskByIdUsesCorrectPath" ;;
  3)  TEST_CLASSES="com.taskflow.controller.TaskControllerTest#testDeleteTaskReturns204" ;;
  4)  TEST_CLASSES="com.taskflow.controller.UserControllerTest#testRegisterEndpointUsesCorrectPath" ;;
  5)  TEST_CLASSES="com.taskflow.controller.UserControllerTest#testGetUserByIdReadsFromPath" ;;
  6)  TEST_CLASSES="com.taskflow.controller.ProjectControllerTest" ;;
  7)  TEST_CLASSES="com.taskflow.controller.CommentControllerTest" ;;
  8)  TEST_CLASSES="com.taskflow.service.TaskServiceTest#testCreateTaskSetsCreatedAt" ;;
  9)  TEST_CLASSES="com.taskflow.service.TaskServiceTest#testUpdateTaskDoesNotThrowForLargeId" ;;
  10) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testGetTasksByUserReturnsOnlyUserTasks" ;;
  11) TEST_CLASSES="NO_TEST" ;;
  12) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testCompleteTaskSetsStatusToCompleted" ;;
  13) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testGetPaginatedTasksDoesNotUseNegativePageIndex" ;;
  14) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testSearchTasksCaseInsensitive" ;;
  15) TEST_CLASSES="com.taskflow.service.UserServiceTest#testFindByUsernameQueriesUsernameField" ;;
  16) TEST_CLASSES="com.taskflow.service.UserServiceTest#testCreateUserDefaultRoleIsUser" ;;
  17) TEST_CLASSES="com.taskflow.service.UserServiceTest#testUpdateUserDoesNotOverwritePasswordWhenNull" ;;
  18) TEST_CLASSES="com.taskflow.service.UserServiceTest#testIsUsernameAvailableReturnsFalseWhenTaken+testIsUsernameAvailableReturnsTrueWhenFree" ;;
  19) TEST_CLASSES="com.taskflow.service.ProjectServiceTest#testGetProjectsByOwnerSortsByCreatedAtDesc" ;;
  20) TEST_CLASSES="com.taskflow.service.ProjectServiceTest#testAddMemberToProjectAddsCorrectUser" ;;
  21) TEST_CLASSES="com.taskflow.service.ProjectServiceTest#testGetTaskCountForProjectCountsOnlyProjectTasks" ;;
  22) TEST_CLASSES="com.taskflow.service.CommentServiceTest#testGetCommentsForTaskFiltersByTaskId" ;;
  23) TEST_CLASSES="com.taskflow.service.CommentServiceTest#testDeleteCommentEnforcesOwnership" ;;
  24) TEST_CLASSES="com.taskflow.mapper.TaskMapperTest#testToDtoMapsPriority+testToDtoMapsAllThreeFields" ;;
  25) TEST_CLASSES="com.taskflow.mapper.TaskMapperTest#testToDtoSetsDueDateCorrectly+testToDtoMapsAllThreeFields" ;;
  26) TEST_CLASSES="com.taskflow.mapper.UserMapperTest+com.taskflow.controller.UserControllerTest#testGetUserResponseDoesNotExposePassword" ;;
  27) TEST_CLASSES="com.taskflow.util.UtilsTest#testIsOverdueReturnsFalseForNow+testIsOverdueReturnsTrueForPast" ;;
  28) TEST_CLASSES="com.taskflow.util.UtilsTest#testGetDaysUntilDueReturnsZeroForOverdue+testGetDaysUntilDueReturnsPositiveForFuture" ;;
  29) TEST_CLASSES="com.taskflow.util.UtilsTest#testIsValidEmailRejectsNoTld+testIsValidEmailAcceptsValidEmail" ;;
  30) TEST_CLASSES="com.taskflow.util.UtilsTest#testIsValidPriorityAcceptsMedium+testIsValidPriorityAcceptsHighAndLow+testIsValidPriorityRejectsInvalid" ;;
  31) TEST_CLASSES="com.taskflow.exception.GlobalExceptionHandlerTest#testResourceNotFoundReturns404" ;;
  32) TEST_CLASSES="com.taskflow.exception.GlobalExceptionHandlerTest#testErrorResponseDoesNotLeakExceptionMessage" ;;
  33) TEST_CLASSES="NO_TEST" ;;
  34) TEST_CLASSES="com.taskflow.model.ModelTest#testTagEqualityForUnsavedInstances+testTagEqualityByName" ;;
  35) TEST_CLASSES="com.taskflow.model.ModelTest#testTaskToStringDoesNotCauseStackOverflow" ;;
  36) TEST_CLASSES="com.taskflow.model.ModelTest#testProjectMemberCascadeDocumented" ;;
  37) TEST_CLASSES="NO_TEST" ;;
  38) TEST_CLASSES="NO_TEST" ;;
  40) TEST_CLASSES="com.taskflow.controller.TaskControllerTest#testGetTasksWithNegativePageReturns400" ;;
  41) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testGetTasksByStatusesUsesAllStatuses" ;;
  42) TEST_CLASSES="com.taskflow.service.UserServiceTest#testCreateUserHashesPasswordWithBCrypt" ;;
  43) TEST_CLASSES="com.taskflow.service.ProjectServiceTest#testRemoveMemberFromProjectPersistsChange" ;;
  44) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testGetOverdueTasksUsesCurrentTime" ;;
  45) TEST_CLASSES="com.taskflow.service.CommentServiceTest#testEditCommentSetsUpdatedAt" ;;
  46) TEST_CLASSES="com.taskflow.mapper.TaskMapperTest#testToDtoMapsProject+testToDtoMapsAllThreeFields" ;;
  47) TEST_CLASSES="NO_TEST" ;;
  48) TEST_CLASSES="com.taskflow.service.CommentServiceTest#testSendTaskAssignedNotificationDoesNotThrow" ;;
  49) TEST_CLASSES="com.taskflow.service.UserServiceTest#testGetPaginatedUsersAppliesSortParameter" ;;
  50) TEST_CLASSES="com.taskflow.service.ProjectServiceTest#testGetProjectStatsCalculatesPercentageCorrectly" ;;
  51) TEST_CLASSES="com.taskflow.service.TaskServiceTest#testBulkUpdateStatusIsTransactional" ;;
  53) TEST_CLASSES="NO_TEST" ;;
  54) TEST_CLASSES="NO_TEST" ;;
  55) TEST_CLASSES="NO_TEST" ;;
  *)
    echo "[ERROR] Unknown issue number: $ISSUE"
    echo "        Valid issues: 1-51 (excl. 11,33,37,38,47), 53-55"
    exit 1
    ;;
esac

###############################################################################
# Handle issues with no automated test
###############################################################################
if [[ "$TEST_CLASSES" == "NO_TEST" ]]; then
  echo ""
  echo "  Issue #${ISSUE} does not have an automated unit test."
  echo "  Verify your fix manually or ask your instructor for guidance."
  exit 0
fi

###############################################################################
# Build the Maven -Dtest= filter expression
# Supports:
#   Single class:              com.foo.BarTest
#   Single method:             com.foo.BarTest#myMethod
#   Multiple with +:           com.foo.BarTest#m1+m2
#   Multiple classes (comma):  com.foo.A,com.foo.B
###############################################################################
# Replace '+' within a method list with '+'  (already correct for Maven)
# Separate multiple entries that are full class paths with ','
MVN_FILTER=""

IFS='+' read -ra METHODS <<< "$TEST_CLASSES"
if [[ ${#METHODS[@]} -eq 1 ]]; then
  # Single entry — may contain a '#' (method) or be a plain class
  # For multiple class names joined by comma, pass through as-is
  MVN_FILTER="$TEST_CLASSES"
else
  # Multiple method specs on the same class: join with '+'
  # e.g. com.foo.BarTest#m1+m2 → Maven accepts this directly
  MVN_FILTER="${TEST_CLASSES}"
fi

# Replace '+' separators between different class#method pairs with ','
# Pattern: if '+' appears between two FQN entries (contains a dot before the +)
# Maven supports comma-separated test specs
MVN_FILTER="${MVN_FILTER//+/,}"
# Restore method separators within the same class: class#method,method2 → class#method+method2
# Maven's -Dtest accepts: ClassName#method1+method2
# Since we collapsed everything with ',' above, this is fine — Maven treats
# comma as "run both" which is what we want.

###############################################################################
# Run Maven
###############################################################################
echo ""
echo "  Running tests for Issue #${ISSUE}..."
echo "  Filter: ${MVN_FILTER}"
echo ""

# Auto-detect mvn vs wrapper
if command -v mvn &>/dev/null; then
  MVN="mvn"
elif [[ -x "$REPO_ROOT/mvnw" ]]; then
  MVN="$REPO_ROOT/mvnw"
else
  echo "[ERROR] Neither 'mvn' nor './mvnw' found. Please install Maven or re-clone the repo."
  exit 1
fi

cd "$REPO_ROOT"
if "$MVN" test -Dtest="${MVN_FILTER}" --no-transfer-progress 2>&1; then
  pass "$ISSUE"
  exit 0
else
  fail "$ISSUE"
  exit 1
fi
